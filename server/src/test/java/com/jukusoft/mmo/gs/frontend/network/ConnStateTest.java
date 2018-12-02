package com.jukusoft.mmo.gs.frontend.network;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ConnStateTest {

    @Test
    public void testConstructor() {
        new ConnState();
    }

    @Test
    public void testGetterAndSetter() {
        ConnState state = new ConnState();

        state.setAuthorized(10, "test", 20, new ArrayList<>());
        assertEquals(10, state.getUserID());
        assertEquals("test", state.getUsername());
        assertEquals(20, state.getCID());

        assertEquals(true, state.listGroups().isEmpty());
        assertEquals(false, state.hasGroup("gamemaster"));

        state.setRegion(2, 3, 4);
        assertEquals(2, state.getRegionID());
        assertEquals(3, state.getInstanceID());
        assertEquals(4, state.getShardID());
    }

}
