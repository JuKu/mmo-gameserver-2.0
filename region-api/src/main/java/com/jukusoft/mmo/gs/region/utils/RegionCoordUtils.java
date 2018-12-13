package com.jukusoft.mmo.gs.region.utils;

import java.util.Objects;

/**
* utils class to calculate hash for hppc IntObjectMap
*/
public class RegionCoordUtils {

    protected RegionCoordUtils () {
        //
    }

    public static int hash (long regionID, int instanceID, int shardID) {
        return Objects.hash(regionID, instanceID, shardID);
    }

}
