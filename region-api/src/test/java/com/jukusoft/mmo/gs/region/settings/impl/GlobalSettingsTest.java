package com.jukusoft.mmo.gs.region.settings.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GlobalSettingsTest {

    @BeforeClass
    public static void beforeClass () {
        //
    }

    @AfterClass
    public static void afterClass () {
        //
    }

    @Test
    public void testConstructor () {
        HazelcastInstance hazelcastInstance = Mockito.mock(HazelcastInstance.class);
        when(hazelcastInstance.getMap(anyString())).thenReturn(Mockito.mock(IMap.class));
        new GlobalSettings(hazelcastInstance);
    }

}
