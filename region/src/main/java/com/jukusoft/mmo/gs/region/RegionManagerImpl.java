package com.jukusoft.mmo.gs.region;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.ObjectArrayList;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.gs.region.utils.RegionCoordUtils;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public class RegionManagerImpl implements RegionManager {

    protected static final int EXPECTED_REGIONS = 16;

    protected static final String LOG_TAG = "RegionMgr";

    protected ObjectArrayList<RegionContainer> regions = new ObjectArrayList<>(EXPECTED_REGIONS);
    protected IntObjectMap<RegionContainer> regionMap = new IntObjectHashMap<>(EXPECTED_REGIONS);

    protected final Vertx vertx;

    public RegionManagerImpl (Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public RegionContainer find(long regionID, int instanceID, int shardID) {
        return this.regionMap.get(RegionCoordUtils.hash(regionID, instanceID, shardID));
    }

    @Override
    public void start(long regionID, int instanceID, int shardID, Handler<RegionContainer> handler) {
        Log.i(LOG_TAG, "start new region " + regionID + ", instanceID: " + instanceID + ", shardID: " + shardID + " on this gameserver instance.");

        RegionContainer container = new RegionContainerImpl(this.vertx, regionID, instanceID, shardID);

        try {
            container.init();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Cannot start region " + regionID + ", instanceID: " + instanceID + ", shardID: " + shardID + " caused by an exception: ", e);
            handler.handle(null);
            return;
        }

        this.regions.add(container);
        this.regionMap.put(RegionCoordUtils.hash(regionID, instanceID, shardID), container);

        if (!container.isInitialized()) {
            Log.w(LOG_TAG, "container.init() was called, but container isn't initialized yet.");
            handler.handle(null);
        }

        Log.i(LOG_TAG, "started region container " + regionID + ", instanceID: " + instanceID + ", shardID: " + shardID + " successfully!");

        handler.handle(container);
    }

}
