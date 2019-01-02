package com.jukusoft.mmo.gs.region.subsystem;

import com.jukusoft.mmo.gs.region.user.User;
import io.vertx.core.json.JsonObject;

public interface SubSystemManager {

    /**
    * add subsystem
     *
     * @param name local unique name of subsystem
     * @param system instance of subsystem
    */
    public void addSubSystem (String name, SubSystem system);

    /**
    * remove subsystem
     *
     * @param system instance of subsystem
    */
    public void removeSubSystem (SubSystem system);

    /**
     * remove subsystem
     *
     * @param name name of subsystem
     */
    public void removeSubSystem (String name);

    /**
    * get a specific subsystem
    */
    public <T extends SubSystem> T getSubSystem (Class<T> cls);

    /**
    * add all static objects to given json object to send to client
    */
    public void fillStaticObjects (JsonObject json, User user, int cid, float posX, float posY, float posZ);

    /**
     * add all game world data to given json object to send to client
     */
    public void fillGameWorldData (JsonObject json, User user, int cid, float posX, float posY, float posZ);

}
