package com.jukusoft.mmo.gs.region.settings.impl;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
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

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object listener = invocation.getArgument(0);
                Objects.requireNonNull(listener);

                if (listener instanceof EntryAddedListener) {
                    ((EntryAddedListener) listener).entryAdded(Mockito.mock(EntryEvent.class));
                } else if (listener instanceof EntryUpdatedListener) {
                    ((EntryUpdatedListener) listener).entryUpdated(Mockito.mock(EntryEvent.class));
                }

                return null;
            }
        }).when(map.addEntryListener(any(MapListener.class), anyBoolean()));

        when(hazelcastInstance.getMap(anyString())).thenReturn(map);
        new GlobalSettings(hazelcastInstance);
    }

}
