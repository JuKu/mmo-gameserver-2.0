package com.jukusoft.mmo.gs.region.subsystem.impl;

import com.jukusoft.mmo.gs.region.subsystem.SubSystem;
import com.jukusoft.mmo.gs.region.user.User;
import io.vertx.core.Vertx;

public class WeatherSubSystem implements SubSystem {

    @Override
    public void init(Vertx vertx, long regionID, int instanceID, int shardID) {
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

}
