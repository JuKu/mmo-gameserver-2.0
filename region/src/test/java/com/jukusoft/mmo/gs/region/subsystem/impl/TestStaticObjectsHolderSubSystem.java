package com.jukusoft.mmo.gs.region.subsystem.impl;

import com.jukusoft.mmo.gs.region.subsystem.StaticObjectsHolder;
import com.jukusoft.mmo.gs.region.subsystem.SubSystemAdapter;
import com.jukusoft.mmo.gs.region.user.User;
import io.vertx.core.json.JsonObject;

public class TestStaticObjectsHolderSubSystem extends SubSystemAdapter implements StaticObjectsHolder {

    @Override
    public void fillStaticObjects(JsonObject json, User user, int cid, float posX, float posY, float posZ) {
        json.put("test", "test2");
    }

}
