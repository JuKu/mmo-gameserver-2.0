package com.jukusoft.mmo.gs.region.database;

import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.gs.region.database.MySQLConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class MySQLConfigTest {

    @BeforeClass
    public static void beforeClass () {
        //clear in-memory config first
        Config.clear();
    }

    @AfterClass
    public static void afterClass () {
        //clear in-memory config first
        Config.clear();
    }

    @Test
    public void testContructor () {
        new MySQLConfig();
    }

    @Test (expected = IllegalStateException.class)
    public void testLoadWithoutConfig () {
        MySQLConfig mySQLConfig = new MySQLConfig();
        mySQLConfig.load();
    }

    @Test
    public void testLoad () throws IOException {
        Config.load(new File("../config/mysql.example.cfg"), false);

        MySQLConfig mySQLConfig = new MySQLConfig();
        mySQLConfig.load();

        assertEquals("localhost", mySQLConfig.getHost());
        assertEquals(3306, mySQLConfig.getPort());
        assertEquals("db1", mySQLConfig.getDatabase());
        assertEquals("root", mySQLConfig.getUser());
        assertEquals("testpass", mySQLConfig.getPassword());
        assertEquals("mmo_", mySQLConfig.getPrefix());

        assertEquals(10, mySQLConfig.getMaxPoolSize());
        assertEquals(true, mySQLConfig.getJDBCUrl().startsWith("jdbc:mysql://"));
        assertEquals(250, mySQLConfig.getPrepStmtCacheSize());
        assertEquals(2048, mySQLConfig.getPrepStmtCacheSqlLimit());

        assertEquals(5000, mySQLConfig.getConnectionTimeout());
        assertEquals(10000, mySQLConfig.getLeakDetectionThreshold());
        assertEquals("true", mySQLConfig.getLogSlowQueries());
        assertEquals("true", mySQLConfig.getDumpQueriesOnException());
    }

    @Test
    public void testGetAndSetPrefix () {
        MySQLConfig mySQLConfig = new MySQLConfig();

        assertEquals("", mySQLConfig.getPrefix());

        mySQLConfig.setPrefix("mmo_");
        assertEquals("mmo_", mySQLConfig.getPrefix());
    }

}
