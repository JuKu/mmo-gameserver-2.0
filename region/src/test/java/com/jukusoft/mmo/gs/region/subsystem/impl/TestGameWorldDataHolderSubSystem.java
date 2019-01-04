package com.jukusoft.mmo.gs.region.subsystem.impl;

import com.jukusoft.mmo.gs.region.subsystem.GameWorldDataHolder;
import com.jukusoft.mmo.gs.region.subsystem.StaticObjectsHolder;
import com.jukusoft.mmo.gs.region.subsystem.SubSystemAdapter;
import com.jukusoft.mmo.gs.region.user.User;
import io.vertx.core.json.JsonObject;

public class TestGameWorldDataHolderSubSystem extends SubSystemAdapter implements GameWorldDataHolder {

    @Override
    public void fillGameWorldData(JsonObject json, User user, int cid, float posX, float posY, float posZ) {
        json.put("test", "test2");
    }

}
