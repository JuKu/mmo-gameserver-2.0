package com.jukusoft.mmo.gs.region;

import com.jukusoft.mmo.engine.shared.config.Cache;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.gs.region.ftp.FTPFactory;
import com.jukusoft.mmo.gs.region.user.User;
import com.jukusoft.mmo.gs.region.utils.FTPUtils;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;
import io.github.bckfnn.ftp.FtpClient;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;

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

        Log.d(LOG_TAG, "download region files from ftp server...");
        FTPFactory.createAsync(ftpClient -> {
            String remoteDir = Config.get("FTP", "regionsDir") + "/region_" + +regionID + "_" + instanceID;
            Log.d(LOG_TAG, "try to download remote ftp directory: " + remoteDir);

            FTPUtils.downloadDir(ftpClient, remoteDir, this.cachePath, res -> {
                if (!res) {
                    Log.e(LOG_TAG, "Coulnd't download region files for region " + this.regionID + ", instanceID: " + this.instanceID + "!");

                    //TODO: disconnect all players and restart region
                } else {
                    ftpFilesLoaded = true;
                    Log.d(LOG_TAG, "all ftp files received for region and stored in cache.");
                }
            });
        });

        this.initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public void initPlayer(User user, int cid) {
        //TODO: send response to proxy / client
    }

    @Override
    public void receive(Buffer buffer, RemoteConnection conn) {
        throw new UnsupportedOperationException("method isn't implemented yet.");
    }

}
