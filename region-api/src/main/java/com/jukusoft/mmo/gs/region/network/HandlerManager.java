package com.jukusoft.mmo.gs.region.network;

import com.jukusoft.vertx.serializer.SerializableObject;

public interface HandlerManager {

    /**
     * find handler for specific message
     *
     * @param cls class name of message
     *
     * @return handler or null, if no handler is registered
     */
    public <T extends SerializableObject> NetMessageHandler<T> findHandler (Class<T> cls);

    public <T extends SerializableObject> void register (Class<T> cls, NetMessageHandler<T> handler);

    public <T extends SerializableObject> void unregister (Class<T> cls);
    
}
