package com.jukusoft.mmo.gs.region;

import com.jukusoft.mmo.gs.region.user.User;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;
import io.vertx.core.buffer.Buffer;

public class RegionContainerImpl implements RegionContainer {

    protected boolean initialized = false;

    public RegionContainerImpl (long regionID, int instanceID, int shardID) {
        //
    }

    @Override
    public void init() {
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
