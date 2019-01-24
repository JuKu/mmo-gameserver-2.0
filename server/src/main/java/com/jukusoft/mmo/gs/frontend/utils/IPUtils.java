package com.jukusoft.mmo.gs.frontend.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPUtils {

    protected IPUtils () {
        //
    }

    public static String getOwnIP () throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        return inetAddress.getHostAddress();
    }

}
