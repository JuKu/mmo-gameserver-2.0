package com.jukusoft.mmo.gs.region.utils;

import com.jukusoft.mmo.gs.region.user.User;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;

import java.util.Objects;

public class PlayerTuple {

    public final User user;
    public final int cid;
    public final RemoteConnection conn;

    public PlayerTuple (User user, int cid, RemoteConnection conn) {
        Objects.requireNonNull(user, "user cannot be null.");
        Objects.requireNonNull(conn, "conn cannot be null.");

        this.user = user;
        this.cid = cid;
        this.conn = conn;
    }

    @Override
    public String toString() {
        return "PlayerTuple{" +
                "username=" + user.getUsername() +
                ", userID=" + user.getUserID() +
                ", cid=" + cid +
                ", conn=" + conn +
                '}';
    }
}
