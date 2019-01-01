package com.jukusoft.mmo.gs.region.settings.impl;

import com.hazelcast.core.*;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapListener;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
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
        IMap map = Mockito.mock(IMap.class);

        when(map.addEntryListener(any(MapListener.class), anyBoolean())).then((Answer<String>) invocation -> {
            Object listener = invocation.getArgument(0);
            Objects.requireNonNull(listener);

            if (listener instanceof EntryAddedListener) {//Object source, Member member, int eventType, K key, V value
                ((EntryAddedListener) listener).entryAdded(new EntryEvent("test", Mockito.mock(Member.class), 1, "test#SPLIT#test2", "test"));
            } else if (listener instanceof EntryUpdatedListener) {
                ((EntryUpdatedListener) listener).entryUpdated(new EntryEvent("test", Mockito.mock(Member.class), 1, "test#SPLIT#test2", "test"));
            }

            return null;
        });

        when(hazelcastInstance.getMap(anyString())).thenReturn(map);
        new GlobalSettings(hazelcastInstance);
    }

}
