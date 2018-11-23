package com.jukusoft.mmo.gs.frontend.utils;

import org.junit.Test;

import java.io.IOException;

public class ConfigLoaderTest {

    @Test
    public void testConstructor () {
        new ConfigLoader();
    }

    @Test
    public void testLoad () throws IOException {
        ConfigLoader.load("../config/", new String[0]);
    }

}
