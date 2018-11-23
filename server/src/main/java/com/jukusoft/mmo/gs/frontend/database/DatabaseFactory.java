package com.jukusoft.mmo.gs.frontend.database;

import com.jukusoft.mmo.engine.shared.logger.Log;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseFactory {

    protected static final String DATABASE_TAG = "Database";

    protected DatabaseFactory () {
        //
    }

    public static Connection build () {
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
        Database.init(mySQLConfig);

        try {
            return Database.getConnection();
        } catch (SQLException e) {
            Log.e(DATABASE_TAG, "Coulnd't get database connection: ", e);
            System.exit(1);

            return null;
        }
    }

}
