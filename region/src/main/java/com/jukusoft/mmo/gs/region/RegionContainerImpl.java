package com.jukusoft.mmo.gs.region;

import com.jukusoft.mmo.engine.shared.config.Cache;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.utils.FileUtils;
import com.jukusoft.mmo.gs.region.ftp.FTPUtil;
import com.jukusoft.mmo.gs.region.ftp.NFtpFactory;
import com.jukusoft.mmo.gs.region.user.User;
import com.jukusoft.mmo.gs.region.utils.PlayerTuple;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;
import io.vertx.core.buffer.Buffer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RegionContainerImpl implements RegionContainer {

    protected final String LOG_TAG;

    //region coordinates
    protected final long regionID;
    protected final int instanceID;
    protected final int shardID;

    //cache path
    protected final String cachePath;

    protected boolean initialized = false;
    protected boolean ftpFilesLoaded = false;
    protected Lock ftpInitLock = new ReentrantLock(true);

    protected Map<String,String> fileHashes = new HashMap<>();

    //queue with players which have connected while region was in initialization process
    protected Queue<PlayerTuple> waitingPlayerInitQueue = new ConcurrentLinkedQueue<>();


    public RegionContainerImpl (long regionID, int instanceID, int shardID) {
        if (regionID <= 0) {
            throw new IllegalArgumentException("regionID has to be >= 1.");
        }

        if (instanceID <= 0) {
            throw new IllegalArgumentException("instanceID has to be >= 1.");
        }

        if (shardID <= 0) {
            throw new IllegalArgumentException("shardID has to be >= 1.");
        }

        this.regionID = regionID;
        this.instanceID = instanceID;
        this.shardID = shardID;

        this.cachePath = Cache.getInstance().getCachePath("regions/region_" + regionID + "_" + instanceID);

        LOG_TAG = "REG_" + regionID + "_" + instanceID + "_" + shardID;
    }

    @Override
    public void init() {
        Log.i(LOG_TAG, "initialize region...");

        //download files for region from ftp server
        this.downloadFilesFromFtp();

        this.initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    private void downloadFilesFromFtp () {
        ftpFilesLoaded = false;

        Log.d(LOG_TAG, "download region files from ftp server...");
        NFtpFactory.createAsync(ftpClient -> {
            //first, clear region cache so files, which was removed on ftp server are also removed here (else only existing files were overriden)
            Log.d(LOG_TAG, "clear region cache: " + this.cachePath);

            try {
                FileUtils.recursiveDeleteDirectory(new File(this.cachePath), false);
            } catch (IOException e) {
                Log.w(LOG_TAG, "Coulnd't delete region cache directory: " + this.cachePath, e);
            }

            String remoteDir = Config.get("FTP", "regionsDir") + "/region_" + +regionID + "_" + instanceID;
            Log.d(LOG_TAG, "try to download remote ftp directory: " + remoteDir);

            try {
                FTPUtil.downloadDirectory(ftpClient, remoteDir, this.cachePath);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Coulnd't download region files for region " + this.regionID + ", instanceID: " + this.instanceID + "!", e);
                return;
            }

            Log.d(LOG_TAG, "all ftp files received for region and stored in cache.");

            //index hashes
            this.indexClientCache();

            ftpInitLock.lock();
            ftpFilesLoaded = true;
            ftpInitLock.unlock();

            //handle all waiting player init entries in queue
        });
    }

    @Override
    public void initPlayer(User user, int cid, RemoteConnection conn) {
        Log.i(LOG_TAG, "init player '" + user.getUsername() + "' (userID: " + user.getUserID() + ") with characterID " + cid);
        PlayerTuple player = new PlayerTuple(user, cid, conn);

        ftpInitLock.lock();

        if (!ftpFilesLoaded) {
            //files aren't downloaded completely yet
            Log.i(LOG_TAG, "wait for downloading all ftp files...");

            this.waitingPlayerInitQueue.add(player);

            return;
        }

        ftpInitLock.unlock();

        //initialize player directly
        this.initPlayer(player);
    }

    private void initPlayer (PlayerTuple player) {
        Log.i(LOG_TAG, "initPlayer(): " + player);

        //TODO: send response to proxy / client
    }

    @Override
    public void receive(Buffer buffer, RemoteConnection conn) {
        throw new UnsupportedOperationException("method isn't implemented yet.");
    }

    private void indexClientCache () {
        this.fileHashes.clear();

        List<String> fileList = new ArrayList<>();
    }

}
