package com.jukusoft.mmo.gs.frontend.network;

/**
* exception which will be thrown if cluster credentials in region join message (from proxy) is wrong, so connection will be closed.
*/
public class WrongClusterCredentialsException extends RuntimeException {

    public WrongClusterCredentialsException (String message) {
        super(message);
    }

}
