package com.jukusoft.mmo.gs.region.settings.impl;

import com.hazelcast.core.*;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapListener;
import com.jukusoft.mmo.gs.region.database.DBClient;
import com.jukusoft.mmo.gs.region.database.Database;
import com.mysql.cj.jdbc.result.ResultSetImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class GlobalSettingsTest {

    @BeforeClass
    public static void beforeClass () throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        Database.setJunitConn(connection);

        DBClient client = Mockito.mock(DBClient.class);
        ResultSet rs = Mockito.mock(ResultSet.class);
        when(rs.getString(anyString())).thenReturn("test");

        AtomicInteger i = new AtomicInteger(2);
        when(rs.next()).then((Answer<Boolean>) invocation -> {
            System.err.println("i = " + i);
            return i.getAndDecrement() > 0;
        });

        when(client.query(anyString())).thenReturn(rs);
        Database.setJunitClient(client);
    }

    @AfterClass
    public static void afterClass () {
        Database.setJunitConn(null);
        Database.setJunitClient(null);
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

    @Test
    public void testConstructor1 () {
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
