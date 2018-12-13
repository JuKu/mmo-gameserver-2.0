package com.jukusoft.mmo.gs.region.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RegionCoordUtilsTest {

    @Test
    public void testConstructor () {
        new RegionCoordUtilsTest();
    }

    @Test
    public void testEqualHashes () {
        int hash1 = RegionCoordUtils.hash(1, 2, 3);
        int hash2 = RegionCoordUtils.hash(1, 2, 3);
        assertEquals(hash1, hash2);

        //test unequal hash with different regionID
        int hash3 = RegionCoordUtils.hash(2, 2, 3);
        assertNotEquals(hash1, hash3);

        //test unequal hash with different instanceID
        int hash4 = RegionCoordUtils.hash(1, 3, 3);
        assertNotEquals(hash1, hash4);

        //test unequal hash with different instanceID
        int hash5 = RegionCoordUtils.hash(1, 2, 4);
        assertNotEquals(hash1, hash5);

        int hash6 = RegionCoordUtils.hash(1, 2, 3);
        assertEquals(hash1, hash6);
    }

}
