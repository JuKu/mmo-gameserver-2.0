package com.jukusoft.mmo.gs.frontend.utils;

import org.junit.Test;

public class VersionPrinterTest {

    @Test
    public void testConstructor () {
        new VersionPrinter();
    }

    @Test
    public void testPrintVersion () {
        VersionPrinter.print();
    }

}
