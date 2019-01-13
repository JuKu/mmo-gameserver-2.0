package com.jukusoft.mmo.gs.region.subsystem.impl.utils;

import com.jukusoft.mmo.gs.region.network.HandlerManager;
import com.jukusoft.mmo.gs.region.network.NetHandlerManager;
import com.jukusoft.mmo.gs.region.network.NetMessageHandler;
import com.jukusoft.mmo.gs.region.subsystem.SubSystem;
import com.jukusoft.mmo.gs.region.user.User;
import com.jukusoft.vertx.serializer.SerializableObject;
import io.vertx.core.Vertx;

import java.util.Objects;

/**
* proxy class for network handlers
 *
 * @see HandlerManager
 * @see NetHandlerManager
 * @see com.jukusoft.mmo.gs.region.RegionContainerImpl
*/
public class NetHandlersProxy implements SubSystem, HandlerManager {

    protected final NetHandlerManager handlerManager;

    public NetHandlersProxy(NetHandlerManager netHandlerManager) {
        Objects.requireNonNull(netHandlerManager);
        this.handlerManager = netHandlerManager;
    }

    @Override
    public void init(Vertx vertx, long regionID, int instanceID, int shardID) {
        //
    }

    @Override
    public boolean processCommand(User user, int cid, String cmd, String[] args) {
        return false;
    }

    @Override
    public void shutdown() {
        //
    }

    public NetHandlerManager handlers () {
        return this.handlerManager;
    }

    @Override
    public <T extends SerializableObject> NetMessageHandler<T> findHandler(Class<T> cls) {
        return handlers().findHandler(cls);
    }

    @Override
    public <T extends SerializableObject> void register(Class<T> cls, NetMessageHandler<T> handler) {
        handlers().register(cls, handler);
    }

    @Override
    public <T extends SerializableObject> void unregister(Class<T> cls) {
        handlers().unregister(cls);
    }
}
