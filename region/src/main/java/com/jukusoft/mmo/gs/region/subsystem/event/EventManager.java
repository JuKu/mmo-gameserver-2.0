package com.jukusoft.mmo.gs.region.subsystem.event;

import com.jukusoft.mmo.gs.region.subsystem.SubSystem;

public interface EventManager extends SubSystem {

    public <T extends Event> void addListener (Class<T> cls, EventListener<T> listener);

    public <T extends Event> void removeListener (Class<T> cls, EventListener<T> listener);

    public <T extends Event> void removeAllListeners (Class<T> cls);

    public <T extends Event> void fire (T event);

}
