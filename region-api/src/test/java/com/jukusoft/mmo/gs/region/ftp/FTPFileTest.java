package com.jukusoft.mmo.gs.region.ftp;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FTPFileTest {

    @Test
    public void testConstructor () {
        new FTPFile();
    }

    @Test
    public void testFrom () {
        FTPFile file = FTPFile.from("drwxr-xr-x   2 ftp1@subdomain.example.com MMORPG       4096 Dec 15 00:38 client");
        assertNotNull(file);

        assertEquals("d", file.getType());
        assertEquals(true, file.isDirectory());
        assertEquals("client", file.getName());
        assertEquals(4096, file.getSize());
        assertEquals("Dec 15 00:38", file.getDate());
    }

    @Test
    public void testFromInvalidFormat () {
        FTPFile file = FTPFile.from("test");
        assertNull(file);
    }

    @Test
    public void testListening () {
        String str =    "drwxr-xr-x   2 ftp1@mmo.jukusoft.com MMORPG       4096 Dec 15 00:38 client\n" +
                        "-rw-r--r--   1 ftp1@mmo.jukusoft.com MMORPG          8 Dec 15 00:51 my-test.txt\n" +
                        "drwxr-xr-x   2 ftp1@mmo.jukusoft.com MMORPG       4096 Dec 15 00:38 server\n" +
                        "invalide line\n" +
                        "drwxr-xr-x   2 ftp1@mmo.jukusoft.com MMORPG       4096 Dec 15 00:38 ..\n" +
                        "drwxr-xr-x   2 ftp1@mmo.jukusoft.com MMORPG       4096 Dec 15 00:38 .\n" +
                        "drwxr-xr-x   2 test-test test 4096 invalide string";

        List<FTPFile> files = FTPFile.listing(str);
        assertEquals(3, files.size());
    }

    @Test
    public void testListening1 () {
        String str =    "drwxr-xr-x   2 ftp1@mmo.jukusoft.com MMORPG       4096 Dec 15 00:38 client\r\n" +
                "-rw-r--r--   1 ftp1@mmo.jukusoft.com MMORPG          8 Dec 15 00:51 my-test.txt\r\n" +
                "drwxr-xr-x   2 ftp1@mmo.jukusoft.com MMORPG       4096 Dec 15 00:38 server";

        List<FTPFile> files = FTPFile.listing(str);
        assertEquals(3, files.size());
    }

}
