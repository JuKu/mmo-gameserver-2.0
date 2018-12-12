package com.jukusoft.mmo.gs.region;

import io.vertx.core.Handler;

public class RegionManagerImpl implements RegionManager {

    @Override
    public RegionContainer find(long regionID, int instanceID, int shardID) {
        return null;
    }

    @Override
    public void start(long regionID, int instanceID, int shardID, Handler<RegionContainer> handler) {
        throw new UnsupportedOperationException("method isn't implemented yet.");
    }

}
