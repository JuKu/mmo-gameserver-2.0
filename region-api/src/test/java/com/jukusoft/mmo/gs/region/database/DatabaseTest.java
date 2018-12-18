package com.jukusoft.mmo.gs.region.database;

import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.gs.region.database.Database;
import com.jukusoft.mmo.gs.region.database.MySQLConfig;
import io.vertx.core.Vertx;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DatabaseTest {

    protected static Vertx vertx = null;

    @BeforeClass
    public static void beforeClass () {
        //clear in-memory config first
        Config.clear();

        vertx = Vertx.vertx();
    }

    @AfterClass
    public static void afterClass () {
        //clear in-memory config first
        Config.clear();

        vertx.close();
    }

    @Test
    public void testConstructor () {
        new Database();
    }

    @Test
    public void testInit () throws IOException, SQLException {
        //load test mysql configuration
        MySQLConfig mySQLConfig = createConfig();

        //initialize database
        Database.init(vertx, mySQLConfig);

        assertNotNull(Database.getDataSource());
        assertNotNull(Database.getConnection());

        //close database connection
        Database.close();
    }

    @Test
    public void testInit1 () throws IOException, SQLException {
        //load test mysql configuration
        MySQLConfig mySQLConfig = createConfig();

        //initialize database
        Database.init("static", vertx, mySQLConfig);

        assertNotNull(Database.getDataSource("static"));
        assertNotNull(Database.getConnection("static"));

        //close database connection
        Database.close();
    }

    @Test
    public void testReplacePrefix () throws IOException {
        //load test mysql configuration
        MySQLConfig mySQLConfig = createConfig();

        //initialize database
        Database.init(vertx, mySQLConfig);

        String query = Database.replacePrefix("SELECT * FROM `{prefix}users`; ");
        assertEquals("SELECT * FROM `mmo_users`; ", query);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetClientWithUnknownName () throws IOException {
        //load test mysql configuration
        MySQLConfig mySQLConfig = createConfig();

        //initialize database
        Database.init(vertx, mySQLConfig);

        Database.getClient("not-existent-name");
    }

    @Test
    public void testGetClient () throws IOException {
        //load test mysql configuration
        MySQLConfig mySQLConfig = createConfig();

        //initialize database
        Database.init("static", vertx, mySQLConfig);
        Database.init(vertx, mySQLConfig);

        assertNotNull(Database.getClient("static"));
        assertNotNull(Database.getClient());
    }

    protected MySQLConfig createConfig () throws IOException {
        //clear in-memory config first
        Config.clear();

        MySQLConfig mySQLConfig = new MySQLConfig();

        //https://docs.travis-ci.com/user/database-setup/#MySQL

        if (new File("../config/mysql.cfg").exists()) {
            Config.load(new File("../config/mysql.cfg"), false);
            mySQLConfig.load();
        } else {
            Config.load(new File("../config/tests/travis.mysql.cfg"), false);
            mySQLConfig.load();
        }

        return mySQLConfig;
    }

}
