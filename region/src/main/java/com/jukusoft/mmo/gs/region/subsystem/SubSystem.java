package com.jukusoft.mmo.gs.region.subsystem;

import com.jukusoft.mmo.gs.region.user.User;
import io.vertx.core.Vertx;

public interface SubSystem {

    /**
    * init subsystem
     *
     * @param vertx singleton vertx instance
     * @param regionID id of region which this subsystem should handle
     * @param instanceID id of instance
     * @param shardID id of shard
    */
    public void init (Vertx vertx, long regionID, int instanceID, int shardID);

    /**
    * process In-Game command from chat
     *
     * @param user user with permissions
     * @param cid character id
     * @param cmd command (e.q. "/setWeather sunny" --> command = "setWeather")
     * @param args array with arguments
     *
     * @return true, if command was processed and belongs to this subsystem (on false iterate over other subsystems)
    */
    public boolean processCommand (User user, int cid, String cmd, String[] args);

    /**
    * shutdown subsystem
    */
    public void shutdown ();

}
