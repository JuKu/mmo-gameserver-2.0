package com.jukusoft.mmo.gs.frontend.utils;

import com.jukusoft.mmo.engine.shared.config.Config;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ConfigLoaderTest {

    @Test
    public void testConstructor () {
        new ConfigLoader();
    }

    @Test
    public void testLoad () throws IOException {
        ConfigLoader.load("../config/", new String[0]);
    }

    @Test
    public void testLoadWithParams () throws IOException {
        ConfigLoader.load("../config/", new String[]{
                "-test",
                "-Config:section1.key2=value3"
        });

        assertEquals("value3", Config.get("section1", "key2"));

        Config.clear();
    }

}
