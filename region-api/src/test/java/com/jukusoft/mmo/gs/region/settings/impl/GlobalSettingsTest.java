package com.jukusoft.mmo.gs.region.settings.impl;

import com.hazelcast.core.*;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapListener;
import com.jukusoft.mmo.gs.region.database.DBClient;
import com.jukusoft.mmo.gs.region.database.Database;
import com.jukusoft.mmo.gs.region.settings.SettingNotExistsException;
import com.jukusoft.mmo.gs.region.settings.Settings;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class GlobalSettingsTest {

    protected static ResultSet rs = null;

    @BeforeClass
    public static void beforeClass () throws SQLException {
        mockDB();
    }

    @AfterClass
    public static void afterClass () {
        Database.setJunitConn(null);
        Database.setJunitClient(null);
    }

    protected static void mockDB () throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        Database.setJunitConn(connection);

        DBClient client = Mockito.mock(DBClient.class);
        ResultSet rs = Mockito.mock(ResultSet.class);
        GlobalSettingsTest.rs = rs;
        when(rs.getString(anyString())).thenReturn("test");

        AtomicInteger i = new AtomicInteger(2);
        when(rs.next()).then((Answer<Boolean>) invocation -> {
            System.err.println("i = " + i);
            return i.getAndDecrement() > 0;
        });

        when(client.query(anyString())).thenReturn(rs);
        Database.setJunitClient(client);
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

    @Test
    public void testConstructor2 () throws SQLException {
        HazelcastInstance hazelcastInstance = getHzInstance();

        when(rs.next()).thenThrow(RuntimeException.class);

        new GlobalSettings(hazelcastInstance);

        mockDB();
    }

    @Test
    public void testInit () {
        GlobalSettings.init(getHzInstance());
    }

    protected HazelcastInstance getHzInstance () {
        HazelcastInstance hazelcastInstance = Mockito.mock(HazelcastInstance.class);
        IMap map = Mockito.mock(IMap.class);

        when(hazelcastInstance.getMap(anyString())).thenReturn(map);

        return hazelcastInstance;
    }

    @Test (expected = IllegalStateException.class)
    public void testGetNotExistentInstance () {
        GlobalSettings.instance = null;
        GlobalSettings.getInstance();
    }

    @Test
    public void testGetInstance () {
        GlobalSettings.instance = Mockito.mock(GlobalSettings.class);

        Settings settings = GlobalSettings.getInstance();
        assertNotNull(settings);

        Settings settings1 = GlobalSettings.getInstance();
        assertNotNull(settings1);

        //check, that instances are the same
        assertEquals(settings, settings1);
    }

    @Test
    public void testGetterAndSetter () {
        Settings settings = new GlobalSettings();
        ((GlobalSettings) settings).clusteredSettingsMap = Mockito.mock(IMap.class);

        settings.set("test", "test1", "test2");
        assertEquals("test2", settings.get("test", "test1"));

        settings.setInt("test", "test3", 1);
        assertEquals("1", settings.get("test", "test3"));
        assertEquals(1, settings.getInt("test", "test3"));
        assertEquals(1, settings.getFloat("test", "test3"), 0.0001f);
    }

    @Test (expected = SettingNotExistsException.class)
    public void testGetNotExistentSettingArea () {
        Settings settings = new GlobalSettings();
        ((GlobalSettings) settings).clusteredSettingsMap = Mockito.mock(IMap.class);

        settings.get("not-existent-area", "key");
    }

    @Test (expected = SettingNotExistsException.class)
    public void testGetNotExistentSettingKey () {
        Settings settings = new GlobalSettings();
        ((GlobalSettings) settings).clusteredSettingsMap = Mockito.mock(IMap.class);

        settings.set("area", "another-key", "test");
        settings.get("area", "not-existent-key");
    }

}
