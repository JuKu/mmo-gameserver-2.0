package com.jukusoft.mmo.gs.frontend.database;

import com.jukusoft.mmo.engine.shared.logger.Log;
import io.vertx.core.Vertx;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseFactory {

    protected static final String DATABASE_TAG = "Database";

    protected DatabaseFactory () {
        //
    }

    public static Connection build (Vertx vertx) {
        Log.i(DATABASE_TAG, "initialize MySQL config...");

        //load mysql config
        MySQLConfig mySQLConfig = new MySQLConfig();
        mySQLConfig.load();

        Log.i(DATABASE_TAG, "execute database upgrader...");

        //create or upgrade database schema
        DatabaseUpgrader databaseUpgrader = new DatabaseUpgrader(mySQLConfig);
        databaseUpgrader.migrate();
        databaseUpgrader.printInfo(DATABASE_TAG);

        Log.i(DATABASE_TAG, "initialize database connection...");

        //initialize database
        Database.init("main", vertx, mySQLConfig);

        //build second connection
        try {
            buildStaticDB(vertx).close();
        } catch (SQLException e) {
            Log.w(DATABASE_TAG, "SQLException while initializing static database: ", e);
        }

        try {
            return Database.getConnection();
        } catch (SQLException e) {
            Log.e(DATABASE_TAG, "Coulnd't get database connection: ", e);
            System.exit(1);

            return null;
        }
    }

    protected static Connection buildStaticDB (Vertx vertx) {
        Log.i(DATABASE_TAG, "initialize static database MySQL config...");

        //load mysql config
        MySQLConfig mySQLConfig = new MySQLConfig();
        mySQLConfig.load("MySQLStaticData");

        Log.i(DATABASE_TAG, "execute database upgrader for static db...");

        //create or upgrade database schema
        DatabaseUpgrader databaseUpgrader = new DatabaseUpgrader(mySQLConfig);
        databaseUpgrader.migrate();
        databaseUpgrader.printInfo(DATABASE_TAG);

        Log.i(DATABASE_TAG, "initialize static database connection...");

        //initialize database
        Database.init("static", vertx, mySQLConfig);

        try {
            return Database.getConnection("static");
        } catch (SQLException e) {
            Log.e(DATABASE_TAG, "Coulnd't get static database connection: ", e);
            System.exit(1);

            return null;
        }
    }

}
