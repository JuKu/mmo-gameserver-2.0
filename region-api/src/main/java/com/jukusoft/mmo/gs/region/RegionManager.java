package com.jukusoft.mmo.gs.region;

import io.vertx.core.Handler;

public interface RegionManager {

    /**
    * get instance of region container or return null, if region isn't loaded on this server
    */
    public RegionContainer find (long regionID, int instanceID, int shardID);

    /**
    * start a new region container
     *
     * @param regionID id of region
     * @param instanceID id of instance
     * @param shardID shardID
     * @param handler asynchronous handler
    */
    public void start (long regionID, int instanceID, int shardID, Handler<RegionContainer> handler);

}
