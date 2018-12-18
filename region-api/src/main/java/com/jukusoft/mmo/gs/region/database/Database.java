package com.jukusoft.mmo.gs.region.database;

import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.vertx.core.Vertx;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;

public class Database {

    //http://www.baeldung.com/hikaricp

    protected static final String LOG_TAG = "Database";

    protected static MySQLConfig mySQLConfig = null;
    protected static HikariDataSource dataSource = null;
    protected static SQLClient mainClient = null;
    protected static ObjectObjectMap<String,SQLClient> clients = new ObjectObjectHashMap<>();

    protected static ObjectObjectMap<String,HikariDataSource> dataSourceMap = new ObjectObjectHashMap<>();

    protected Database() {
        //
    }

    public static void init (Vertx vertx, MySQLConfig mySQLConfig) {
        init("main", vertx, mySQLConfig);
    }

    public static void init (String name, Vertx vertx, MySQLConfig mySQLConfig) {
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

        //set max pool size
        config.setMaximumPoolSize(mySQLConfig.getMaxPoolSize());

        // We will wait for 15 seconds to get a connection from the pool.
        // Default is 30, but it shouldn't be taking that long.
        config.setConnectionTimeout(mySQLConfig.getConnectionTimeout());

        // If a connection is not returned within 10 seconds, it's probably safe to assume it's been leaked.
        config.setLeakDetectionThreshold(mySQLConfig.getLeakDetectionThreshold());

        //recommended default configuration, see https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        config.addDataSourceProperty("logSlowQueries", mySQLConfig.getLogSlowQueries());

        if (name.equals("main")) {
            dataSource = new HikariDataSource(config);
            mainClient = JDBCClient.create(vertx, dataSource);
        } else {
            HikariDataSource dataSource1 = new HikariDataSource(config);
            dataSourceMap.put(name, dataSource1);
            clients.put(name, JDBCClient.create(vertx, dataSource1));
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
        Objects.requireNonNull(name);

        if (name.equals("main")) {
            return dataSource.getConnection();
        } else {
            if (dataSourceMap.containsKey(name)) {
                return dataSourceMap.get(name).getConnection();
            } else {
                throw new IllegalArgumentException("client with name '" + name + "' doesn't exists!");
            }
        }
    }

    public static DBClient getClient () {
        return getClient("main");
    }

    public static DBClient getClient (String name) {
        Objects.requireNonNull(name);

        try {
            Connection connection = getConnection(name);
            return new SimpleDBClient(connection, mySQLConfig.getPrefix());
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Cannot get DBClient: ", e);
            throw new IllegalStateException("Cannot get DBClient: ", e);
        }
    }

    public static void close () {
        dataSource.close();

        //close all other data sources too
        dataSourceMap.forEach((Consumer<ObjectObjectCursor<String, HikariDataSource>>) cursor -> {
            cursor.value.close();
        });
    }

    public static String replacePrefix (String query) {
        return query.replace("{prefix}", mySQLConfig.getPrefix());
    }

}
