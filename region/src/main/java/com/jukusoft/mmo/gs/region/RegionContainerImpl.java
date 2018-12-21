package com.jukusoft.mmo.gs.region;

import com.jukusoft.mmo.engine.shared.config.Cache;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.memory.Pools;
import com.jukusoft.mmo.engine.shared.messages.DownloadRegionFilesRequest;
import com.jukusoft.mmo.engine.shared.messages.LoadMapResponse;
import com.jukusoft.mmo.engine.shared.utils.FileUtils;
import com.jukusoft.mmo.engine.shared.utils.HashUtils;
import com.jukusoft.mmo.gs.region.database.DBClient;
import com.jukusoft.mmo.gs.region.database.Database;
import com.jukusoft.mmo.gs.region.database.InvalideDatabaseException;
import com.jukusoft.mmo.gs.region.ftp.FTPUtil;
import com.jukusoft.mmo.gs.region.ftp.NFtpFactory;
import com.jukusoft.mmo.gs.region.handler.FileUpdaterHandler;
import com.jukusoft.mmo.gs.region.network.NetHandlerManager;
import com.jukusoft.mmo.gs.region.network.NetMessageHandler;
import com.jukusoft.mmo.gs.region.user.User;
import com.jukusoft.mmo.gs.region.utils.PlayerTuple;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;
import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.Serializer;
import com.jukusoft.vertx.serializer.utils.ByteUtils;
import io.vertx.core.buffer.Buffer;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    //region attributes from static db
    protected String regionTitle = "";
    protected boolean locked = false;

    //cache path
    protected final String cachePath;

    protected boolean initialized = false;
    protected boolean ftpFilesLoaded = false;
    protected Lock ftpInitLock = new ReentrantLock(true);

    protected Map<String,String> fileHashes = new HashMap<>();

    //queue with players which have connected while region was in initialization process
    protected Queue<PlayerTuple> waitingPlayerInitQueue = new ConcurrentLinkedQueue<>();

    //message handler manager
    private final NetHandlerManager handlerManager = new NetHandlerManager();

    //sql queries
    protected static final String SQL_GET_REGION = "SELECT * FROM `{prefix}regions` WHERE `regionID` = ? AND `instanceID` = ?; ";

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

    /**
    * constructor for junit tests
    */
    protected RegionContainerImpl (String cachePath) {
        this.regionID = 1;
        this.instanceID = 1;
        this.shardID = 1;

        this.LOG_TAG = "RegionCont";

        this.cachePath = cachePath;
    }

    @Override
    public void init() {
        Log.i(LOG_TAG, "initialize region...");

        //download files for region from ftp server
        this.downloadFilesFromFtp();

        //load static region data from static database (they are fixed and cannot be changed at runtime - only with updates)
        this.loadStaticDataFromDB();

        //register message handlers
        this.handlers().register(DownloadRegionFilesRequest.class, new FileUpdaterHandler(LOG_TAG));

        //TODO: load scripts and so on

        Log.i(LOG_TAG, "initialized region '" + this.regionTitle + "' successfully!");

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
            try {
                this.indexClientCache();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Coulnd't index region ftp files: " + this.cachePath, e);
                return;
            }

            ftpInitLock.lock();
            ftpFilesLoaded = true;
            ftpInitLock.unlock();

            //handle all waiting player init entries in queue
            PlayerTuple player = this.waitingPlayerInitQueue.poll();

            while (player != null) {
                this.initPlayer(player);

                //get next entry from queue
                player = this.waitingPlayerInitQueue.poll();
            }
        });
    }

    @Override
    public void initPlayer(User user, int cid, RemoteConnection conn) {
        Objects.requireNonNull(user, "user cannot be null.");
        Objects.requireNonNull(conn, "conn cannot be null.");

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

    @Override
    public void logoutPlayer(User user, int cid) {
        //TODO: add code here
    }

    private void initPlayer (PlayerTuple player) {
        Log.i(LOG_TAG, "initPlayer(): " + player);

        //send response to proxy / client
        LoadMapResponse response = Pools.get(LoadMapResponse.class);
        response.regionID = this.regionID;
        response.instanceID = this.instanceID;
        response.regionTitle = this.regionTitle;

        //add all required files with file checksums, so client can check, if map files are up to date
        for (Map.Entry<String,String> entry : this.fileHashes.entrySet()) {
            response.addRequiredMap(entry.getKey(), entry.getValue());
        }

        //TODO: load character data

        //TODO: load last character position from database (if it not exists --> character has joined the first time to this game)

        Log.d(LOG_TAG, "send LoadMapResponse to client.");

        //send message back to client
        player.conn.send(response);
    }

    @Override
    public void receive(Buffer buffer, User user, int cid, RemoteConnection conn) {
        if (!this.initialized || !this.ftpFilesLoaded) {
            throw new IllegalStateException("region isn't initialized yet.");
        }

        //unserialize message
        SerializableObject object = Serializer.unserialize(buffer);

        NetMessageHandler handler = handlers().findHandler(object.getClass());

        if (handler == null) {
            Log.w(LOG_TAG, "no handler is registered for message type " + ByteUtils.byteToHex(buffer.getByte(0)) + ", extendedType: " + ByteUtils.byteToHex(buffer.getByte(1)));
            throw new UnsupportedOperationException("no handler is registered for message type " + ByteUtils.byteToHex(buffer.getByte(0)) + ", extendedType: " + ByteUtils.byteToHex(buffer.getByte(1)));
        }

        //call handler
        handler.receive(object, user, cid, conn);
    }

    protected NetHandlerManager handlers() {
        return this.handlerManager;
    }

    protected void indexClientCache () throws Exception {
        this.fileHashes.clear();

        List<String> fileList = FileUtils.listFiles(new File(this.cachePath + "client/"));

        for (String filePath : fileList) {
            File f = new File(this.cachePath + "client/" + filePath);

            if (!f.exists()) {
                throw new IOException("Cannot index file, file doesn't exists: " + f.getAbsolutePath());
            }

            //calculate hash
            String fileHash = HashUtils.computeMD5FileHash(f);
            Log.v(LOG_TAG, "calculated hash for file '" + filePath + "': " + fileHash);

            this.fileHashes.put(filePath, fileHash);
        }
    }

    protected void loadStaticDataFromDB () {
        Log.i(LOG_TAG, "load static data from db...");

        try (DBClient dbClient = Database.getClient("static")) {
            try (PreparedStatement statement = dbClient.prepareStatement(SQL_GET_REGION)) {
                //set params
                statement.setLong(1, this.regionID);
                statement.setInt(2, this.instanceID);

                int counter = 0;

                // execute select SQL stetement
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        this.regionTitle = rs.getString("title");
                        this.locked = rs.getInt("locked") == 1;

                        //increment counter so we can verify, that min one result is available --> row exists
                        counter++;
                    }
                }

                if (counter <= 0) {
                    throw new InvalideDatabaseException("regionID " + this.regionID + ", instanceID: " + this.instanceID + " doesn't exists in database!");
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Cannot initialize region, because of SQLException while loading region static data from db: ", e);
            return;
        }

        Log.i(LOG_TAG, "loaded static data successfully!");
    }

}
