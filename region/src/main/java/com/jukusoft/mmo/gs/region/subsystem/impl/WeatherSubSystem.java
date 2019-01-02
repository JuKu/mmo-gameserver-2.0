package com.jukusoft.mmo.gs.region.subsystem.impl;

import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.gs.region.subsystem.GameWorldDataHolder;
import com.jukusoft.mmo.gs.region.subsystem.SubSystem;
import com.jukusoft.mmo.gs.region.subsystem.SubSystemAdapter;
import com.jukusoft.mmo.gs.region.user.User;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class WeatherSubSystem extends SubSystemAdapter implements GameWorldDataHolder {

    protected static final String LOG_TAG = "WeatherSys";

    @Override
    public boolean processCommand(User user, int cid, String cmd, String[] args) {
        return false;
    }

    @Override
    public void fillGameWorldData(JsonObject json, User user, int cid, float posX, float posY, float posZ) {
        Log.v(LOG_TAG, "fillGameWorldData for user '" + user.getUserID() + "' with cid " + cid);

        //TODO: add current weather & day / night (lighting) to json object, so that client knows current weather
    }

    @Override
    public void shutdown() {
        //
    }

}
