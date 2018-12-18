package com.jukusoft.mmo.gs.region;

import com.jukusoft.mmo.gs.region.user.User;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;
import io.vertx.core.buffer.Buffer;

public interface RegionContainer {

    /**
    * initialize region container and load config from database
    */
    public void init();

    /**
    * check, if region is initialized
     *
     * @return true, if region was initialized yet
    */
    public boolean isInitialized();

    /**
    * join player to this region
     *
     * @param user current user which plays this character (this hasn't to be the owner of the character, this can also be a gamemaster!)
     * @param cid character id
    */
    public void initPlayer(User user, int cid, RemoteConnection conn);

    /**
    * player has closed the connection, so logout player
     *
     * @param user user instance
     * @param cid character id
    */
    public void logoutPlayer(User user, int cid);

    /**
    * receive network message
     *
     * @param buffer network message
     * @param conn network connection to client (will be redirected automatically from proxy server)
    */
    public void receive(Buffer buffer, RemoteConnection conn);

}
