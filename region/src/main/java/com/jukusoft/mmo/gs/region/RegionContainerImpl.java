package com.jukusoft.mmo.gs.region;

import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.gs.region.user.User;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;
import io.vertx.core.buffer.Buffer;

public class RegionContainerImpl implements RegionContainer {

    protected final String LOG_TAG;

    protected final long regionID;
    protected final int instanceID;
    protected final int shardID;

    protected boolean initialized = false;

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

        LOG_TAG = "REG_" + regionID + "_" + instanceID + "_" + shardID;
    }

    @Override
    public void init() {
        Log.i(LOG_TAG, "initialize region...");

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
