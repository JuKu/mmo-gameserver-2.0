package com.jukusoft.mmo.gs.region.settings;

public interface Settings {

    public void set (String area, String key, String value);

    public void setInt (String area, String key, int value);

    public void setFloat (String area, String key, float value);

    public void setBool (String area, String key, boolean value);

    public String get (String area, String key) throws SettingNotExistsException;

    public int getInt (String area, String key) throws SettingNotExistsException;

    public float getFloat (String area, String key) throws SettingNotExistsException;

    public boolean getBoolean (String area, String key) throws SettingNotExistsException;

}
