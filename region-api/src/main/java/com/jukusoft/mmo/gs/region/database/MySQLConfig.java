package com.jukusoft.mmo.gs.region.database;

import com.jukusoft.mmo.engine.shared.config.Config;

public class MySQLConfig {

    protected static final String SECTION_NAME = "MySQL";

    protected String host = "locahost";
    protected int port = 3306;
    protected String database = "";
    protected String user = "";
    protected String password = "";
    protected String prefix = "";
    protected int maxPoolSize = 30;

    protected int prepStmtCacheSize = 250;
    protected int prepStmtCacheSqlLimit = 2048;

    protected int connectionTimeout = 0;
    protected int leakDetectionThreshold = 0;

    //for database upgrader
    protected String migrationPath = "classpath:db/migration";

    //logging settings
    protected String logSlowQueries = "true";
    protected String dumpQueriesOnException = "true";

    public MySQLConfig() {
        //
    }

    public void load(String section) {
        this.host = Config.get(section, "host");
        this.port = Config.getInt(section, "port");
        this.database = Config.get(section, "database");
        this.user = Config.get(section, "user");
        this.password = Config.get(section, "password");
        this.prefix = Config.get(section, "prefix");

        this.maxPoolSize = Config.getInt(section, "max_pool_size");
        this.prepStmtCacheSize = Config.getInt(section, "prepStmtCacheSize");
        this.prepStmtCacheSqlLimit = Config.getInt(section, "prepStmtCacheSqlLimit");

        this.connectionTimeout = Config.getInt(section, "connectionTimeout");
        this.leakDetectionThreshold = Config.getInt(section, "leakDetectionThreshold");

        this.migrationPath = Config.get(section, "flyway_location");
        this.logSlowQueries = Config.get(section, "logSlowQueries");
        this.dumpQueriesOnException = Config.get(section, "dumpQueriesOnException");
    }

    public void load() {
        this.load(SECTION_NAME);
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getMaxPoolSize() {
        return this.maxPoolSize;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getLeakDetectionThreshold() {
        return leakDetectionThreshold;
    }

    public String getJDBCUrl() {
        return "jdbc:mysql://" + this.getHost() + ":" + this.getPort() + "/" + this.getDatabase() + "?autoreconnect=true&serverTimezone=UTC&zeroDateTimeBehavior=convertToNull";
    }

    public int getPrepStmtCacheSize() {
        return prepStmtCacheSize;
    }

    public int getPrepStmtCacheSqlLimit() {
        return prepStmtCacheSqlLimit;
    }

    public String getFlywayLocation() {
        return this.migrationPath;
    }

    public String getLogSlowQueries() {
        return logSlowQueries;
    }

    public String getDumpQueriesOnException() {
        return dumpQueriesOnException;
    }

}
