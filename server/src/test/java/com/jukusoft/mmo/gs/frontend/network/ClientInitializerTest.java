package com.jukusoft.mmo.gs.frontend.network;

import com.jukusoft.mmo.gs.region.RegionContainer;
import com.jukusoft.mmo.gs.region.RegionManager;
import org.junit.Test;

public class ClientInitializerTest {

    @Test (expected = NullPointerException.class)
    public void testNullConstructor () {
        new ClientInitializer(null);
    }

    @Test
    public void testConstructor () {
        new ClientInitializer(new RegionManager() {
            @Override
            public RegionContainer find(long regionID, int instanceID, int shardID) {
                return null;
            }
        });
    }

}
