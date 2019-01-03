package com.jukusoft.mmo.gs.region.subsystem;

import com.jukusoft.mmo.gs.region.user.User;
import io.vertx.core.json.JsonObject;

public interface SubSystemManager {

    /**
    * add subsystem
     *
     * @param cls local unique class of subsystem (can also be the interface of this subsystem which will be injected by other subsystems)
     * @param system instance of subsystem
    */
    public <T extends SubSystem> void addSubSystem (Class<T> cls, T system);

    /**
    * remove subsystem
     *
     * @param system instance of subsystem
    */
    public void removeSubSystem (SubSystem system);

    /**
     * remove subsystem
     *
     * @param cls class of subsystem to remove
     */
    public <T extends SubSystem> void removeSubSystem (Class<T> cls);

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
