package com.jukusoft.mmo.gs.frontend.network;

/**
* exception which will be thrown if gameserver receives a message from a client which isn't authentificated yet, so message will be dropped.
*/
public class UnauthentificatedException extends RuntimeException {

    public UnauthentificatedException (String message) {
        super(message);
    }

}
