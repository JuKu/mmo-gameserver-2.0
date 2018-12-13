package com.jukusoft.mmo.gs.frontend.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VersionPrinterTest {

    @Test
    public void testConstructor () {
        new VersionPrinter();
    }

    @Test
    public void testPrintVersion () {
        VersionPrinter.print();
    }

    @Test
    public void testGetOrDefault () {
        assertEquals("test", VersionPrinter.getOrDefault("test", "test2"));
        assertEquals("test2", VersionPrinter.getOrDefault("n/a", "test2"));
    }

}
