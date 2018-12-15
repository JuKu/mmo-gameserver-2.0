package com.jukusoft.mmo.gs.region.utils;

import io.vertx.core.Handler;

public class DummyHandler<T> implements Handler<T> {

    @Override
    public void handle(T event) {
        //don't do anything here
    }

}
