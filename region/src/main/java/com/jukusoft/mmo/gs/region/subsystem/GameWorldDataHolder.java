package com.jukusoft.mmo.gs.region.subsystem;

import com.jukusoft.mmo.gs.region.user.User;
import io.vertx.core.json.JsonObject;

public interface GameWorldDataHolder {

    /**
     * add all game world data to given json object to send to client
     */
    public void fillGameWorldData (JsonObject json, User user, int cid, float posX, float posY, float posZ);

}
