package com.jukusoft.mmo.gs.region.subsystem;

import com.jukusoft.mmo.gs.region.user.User;
import io.vertx.core.Vertx;

public abstract class SubSystemAdapter implements SubSystem {

    private Vertx vertx = null;
    private long regionID = 0;
    private int instanceID = 0;
    private int shardID = 0;

    @Override
    public final void init(Vertx vertx, long regionID, int instanceID, int shardID) {
        this.vertx = vertx;
        this.regionID = regionID;
        this.instanceID = instanceID;
        this.shardID = shardID;

        this.onInit();
    }

    protected void onInit () {
        //
    }

    @Override
    public boolean processCommand(User user, int cid, String cmd, String[] args) {
        return false;
    }

    @Override
    public void shutdown() {
        //
    }

    protected Vertx getVertx() {
        return vertx;
    }

    protected long getRegionID() {
        return regionID;
    }

    protected int getInstanceID() {
        return instanceID;
    }

    protected int getShardID() {
        return shardID;
    }
}
