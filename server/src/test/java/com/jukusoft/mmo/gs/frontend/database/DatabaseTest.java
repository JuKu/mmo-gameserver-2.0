package com.jukusoft.mmo.gs.frontend.database;

import com.jukusoft.mmo.engine.shared.config.Config;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DatabaseTest {

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
    public void testConstructor () {
        new Database();
    }

    @Test
    public void testInit () throws IOException, SQLException {
        //load test mysql configuration
        MySQLConfig mySQLConfig = createConfig();

        //initialize database
        Database.init(mySQLConfig);

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
        Database.init("static", mySQLConfig);

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
        Database.init(mySQLConfig);

        String query = Database.replacePrefix("SELECT * FROM `{prefix}users`; ");
        assertEquals("SELECT * FROM `mmo_users`; ", query);
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
