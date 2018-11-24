package com.jukusoft.mmo.gs.frontend.database;

import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {

    //http://www.baeldung.com/hikaricp

    protected static final String LOG_TAG = "Database";

    protected static MySQLConfig mySQLConfig = null;
    protected static HikariDataSource dataSource = null;

    protected static ObjectObjectMap<String,HikariDataSource> dataSourceMap = new ObjectObjectHashMap<>();

    protected Database() {
        //
    }

    public static void init (MySQLConfig mySQLConfig) {
        init("main", mySQLConfig);
    }

    public static void init (String name, MySQLConfig mySQLConfig) {
        Database.mySQLConfig = mySQLConfig;

        Log.i(LOG_TAG, "connect to mysql database: " + mySQLConfig.getJDBCUrl());

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(mySQLConfig.getJDBCUrl());
        config.setUsername(mySQLConfig.getUser());
        config.setPassword(mySQLConfig.getPassword());

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "" + mySQLConfig.getPrepStmtCacheSize());
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "" + mySQLConfig.getPrepStmtCacheSqlLimit());

        config.addDataSourceProperty("useServerPrepStmts", "true");//Newer versions of MySQL support server-side prepared statements, this can provide a substantial performance boost. Set this property to true.

        //recommended default configuration, see https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        if (name.equals("main")) {
            dataSource = new HikariDataSource(config);
        } else {
            dataSourceMap.put(name, dataSource);
        }

        Log.i(LOG_TAG, "connection established.");
    }

    public static HikariDataSource getDataSource () {
        return getDataSource("main");
    }

    public static HikariDataSource getDataSource (String name) {
        if (name.equals("main")) {
            return dataSource;
        } else {
            return dataSourceMap.get(name);
        }
    }

    public static Connection getConnection () throws SQLException {
        return getConnection("main");
    }

    public static Connection getConnection (String name) throws SQLException {
        if (name.equals("main")) {
            return dataSource.getConnection();
        } else {
            return dataSourceMap.get(name).getConnection();
        }
    }

    public static void close () {
        dataSource.close();
    }

    public static String replacePrefix (String query) {
        return query.replace("{prefix}", mySQLConfig.getPrefix());
    }

}
