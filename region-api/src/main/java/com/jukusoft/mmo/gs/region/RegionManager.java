package com.jukusoft.mmo.gs.region;

public interface RegionManager {

    /**
    * get instance of region container or return null, if region isn't loaded on this server
    */
    public RegionContainer find (long regionID, int instanceID, int shardID);

}
