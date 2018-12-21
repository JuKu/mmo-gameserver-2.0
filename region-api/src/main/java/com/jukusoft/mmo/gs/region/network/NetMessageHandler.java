package com.jukusoft.mmo.gs.region.network;

import com.jukusoft.mmo.gs.region.user.User;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;
import com.jukusoft.vertx.serializer.SerializableObject;

@FunctionalInterface
public interface NetMessageHandler<T extends SerializableObject> {

    public void receive(T msg, User user, int cid, RemoteConnection conn);

}
