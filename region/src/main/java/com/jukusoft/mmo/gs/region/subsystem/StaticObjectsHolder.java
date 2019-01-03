package com.jukusoft.mmo.gs.region.subsystem;

import com.jukusoft.mmo.gs.region.user.User;
import io.vertx.core.json.JsonObject;

public interface StaticObjectsHolder {

    /**
     * add all static objects to given json object to send to client
     *
     * @param json json object where data should be added
     * @param user user instance
     * @param cid character id
     * @param posX posX
     * @param posY posY
     * @param posZ posZ
     */
    public void fillStaticObjects (JsonObject json, User user, int cid, float posX, float posY, float posZ);

}
