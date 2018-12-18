package com.jukusoft.mmo.gs.region.utils;

import com.jukusoft.mmo.gs.region.user.User;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PlayerTupleTest {

    @Test
    public void testConstructor () {
        new PlayerTuple(Mockito.mock(User.class), 1, Mockito.mock(RemoteConnection.class));
    }

    @Test
    public void testToString () {
        PlayerTuple player = new PlayerTuple(Mockito.mock(User.class), 1, Mockito.mock(RemoteConnection.class));

        assertNotNull(player.toString());

        //check if it contains cid
        assertEquals(true, player.toString().contains("1"));
    }

}
