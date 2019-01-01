package com.jukusoft.mmo.gs.region.settings.impl;

import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.gs.region.database.DBClient;
import com.jukusoft.mmo.gs.region.database.Database;
import com.jukusoft.mmo.gs.region.settings.SettingNotExistsException;
import com.jukusoft.mmo.gs.region.settings.Settings;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class GlobalSettings implements Settings {

    protected static final String LOG_TAG = "GlobalSettings";
    protected static final String SQL_GET_QUERY = "SELECT * FROM `{prefix}global_settings` WHERE `load_on_init` = 1; ";
    protected static final String SQL_INSERT_QUERY = "INSERT INTO `{prefix}global_settings` (" +
            "`area`, `key`, `value`, `load_on_init`" +
            ") VALUES (" +
            "?, ?, ?, 1" +
            ") ON DUPLICATE KEY UPDATE `value` = ?; ";

    //singleton instance
    protected static GlobalSettings instance = null;

    protected ObjectObjectMap<String,ObjectObjectMap<String,String>> settings = new ObjectObjectHashMap<>(100);
    protected IMap<String,String> clusteredSettingsMap = null;

    public GlobalSettings (HazelcastInstance hazelcastInstance) {
        //load all global settings from database
        load(hazelcastInstance);
    }

    protected void load (HazelcastInstance hazelcastInstance) {
        this.clusteredSettingsMap = hazelcastInstance.getMap("global-settings-cache");

        //add cache listeners to update local cache, if neccessary (without reading from database again)
        this.clusteredSettingsMap.addEntryListener((EntryAddedListener<String, String>) event -> {
            //setting was added
            setLocal(event.getKey(), event.getValue());
        }, true);

        this.clusteredSettingsMap.addEntryListener((EntryUpdatedListener<String, String>) event -> {
            //setting was changed
            setLocal(event.getKey(), event.getValue());
        }, true);

        //load all settings from database
        try (DBClient client = Database.getClient()) {
            try (ResultSet rs = client.query(SQL_GET_QUERY)) {
                while (rs.next()) {
                    String area = rs.getString("area");
                    String key = rs.getString("key");
                    String value = rs.getString("value");

                    //update cluster cache
                    this.setInClusterCache(area, key, value);

                    //update local cache
                    this.setLocal(area, key, value);
                }
            }
        } catch (Exception e) {
            Log.w(LOG_TAG, "Exception while loading global settings: ", e);
        }
    }

    @Override
    public void set(String area, String key, String value) {
        //update in DB
        try (DBClient client = Database.getClient()) {
            try (PreparedStatement statement = client.prepareStatement(SQL_INSERT_QUERY)) {
                statement.setString(1, area);
                statement.setString(2, key);
                statement.setString(3, value);
                statement.setString(4, value);
            }
        } catch (Exception e) {
            Log.w(LOG_TAG, "Exception while set global setting: ", e);
        }

        //update in cluster cache
        this.setInClusterCache(area, key, value);

        //update locally
        setLocal(area, key, value);
    }

    protected void setLocal (String area, String key, String value) {
        if (this.settings.get(area) == null) {
            this.settings.put(area, new ObjectObjectHashMap<>());
        }

        //add setting to local map
        ObjectObjectMap<String,String> keyValueMap = this.settings.get(area);
        keyValueMap.put(key, value);
    }

    protected void setLocal (String area_key, String value) {
        String array[] = area_key.split("#SPLIT#");
        setLocal(array[0], array[1], value);
    }

    protected void setInClusterCache (String area, String key, String value) {
        //update cluster cache
        clusteredSettingsMap.putAsync(area + "#SPLIT#" + key, value);
    }

    @Override
    public void setInt(String area, String key, int value) {
        set(area, key, "" + value);
    }

    @Override
    public void setFloat(String area, String key, float value) {
        set(area, key, "" + value);
    }

    @Override
    public void setBool(String area, String key, boolean value) {
        set(area, key, "" + (value ? "true" : "false"));
    }

    @Override
    public String get(String area, String key) throws SettingNotExistsException {
        return null;
    }

    @Override
    public int getInt(String area, String key) throws SettingNotExistsException {
        return Integer.parseInt(get(area, key));
    }

    @Override
    public float getFloat(String area, String key) throws SettingNotExistsException {
        return Float.parseFloat(get(area, key));
    }

    @Override
    public boolean getBoolean(String area, String key) throws SettingNotExistsException {
        return get(area, key).equals("true") ? true : false;
    }

    public static Settings getInstance () {
        if (instance == null) {
            throw new IllegalStateException("GlobalSettings isn't initialized yet, call GlobalSettings.init() first!");
        }

        return instance;
    }

    public static void init (HazelcastInstance hazelcastInstance) {
        Objects.requireNonNull(hazelcastInstance);

        if (instance != null) {
            throw new IllegalStateException("GlobalSettings are already initialized, cannot initialize settings twice!");
        }

        instance = new GlobalSettings(hazelcastInstance);
    }

}
