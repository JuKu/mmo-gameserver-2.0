package com.jukusoft.mmo.gs.frontend.network;

import com.jukusoft.mmo.gs.region.RegionManager;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;
import com.jukusoft.vertx.connection.stream.BufferStream;
import org.junit.Test;
import org.mockito.Mockito;

public class ClientInitializerTest {

    @Test
    public void testConstructor () {
        new ClientInitializer(Mockito.mock(RegionManager.class));
    }

    @Test
    public void testHandleConnect () {
        ClientInitializer clientInitializer = new ClientInitializer(Mockito.mock(RegionManager.class));
        clientInitializer.handleConnect(Mockito.mock(BufferStream.class), Mockito.mock(RemoteConnection.class));
    }

}
