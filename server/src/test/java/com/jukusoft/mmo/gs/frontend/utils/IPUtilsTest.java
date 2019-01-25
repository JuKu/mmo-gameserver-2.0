package com.jukusoft.mmo.gs.frontend.utils;

import org.junit.Test;

import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

public class IPUtilsTest {

    @Test
    public void testConstructor () {
        new IPUtils();
    }

    @Test
    public void testGetOwnIP () throws UnknownHostException {
        String ip = IPUtils.getOwnIP();

        //check, that no localhost or loopback ip is returned
        assertEquals("IP cannot be 127.0.0.1 .", false, ip.equals("127.0.0.1"));
        assertEquals("IP cannot start with 127.", false, ip.startsWith("127."));
    }

}
