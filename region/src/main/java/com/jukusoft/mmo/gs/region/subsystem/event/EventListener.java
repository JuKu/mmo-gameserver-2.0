package com.jukusoft.mmo.gs.region.subsystem.event;

@FunctionalInterface
public interface EventListener<T extends Event> {

    /**
     * handle event
     *
     * @param event single event
     */
    public void handleEvent (T event);

}
