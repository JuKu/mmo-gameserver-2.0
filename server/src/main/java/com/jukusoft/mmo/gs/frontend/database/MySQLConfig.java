package com.jukusoft.mmo.gs.frontend.database;

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

    public MySQLConfig() {
        //
    }

    public void load () {
        this.host = Config.get(SECTION_NAME, "host");
        this.port = Config.getInt(SECTION_NAME, "port");
        this.database = Config.get(SECTION_NAME, "database");
        this.user = Config.get(SECTION_NAME, "user");
        this.password = Config.get(SECTION_NAME, "password");
        this.prefix = Config.get(SECTION_NAME, "prefix");

        this.maxPoolSize = Config.getInt(SECTION_NAME, "max_pool_size");
        this.prepStmtCacheSize = Config.getInt(SECTION_NAME, "prepStmtCacheSize");
        this.prepStmtCacheSqlLimit = Config.getInt(SECTION_NAME, "prepStmtCacheSqlLimit");
    }

    public String getHost () {
        return this.host;
    }

    public int getPort () {
        return this.port;
    }

    public String getDatabase () {
        return this.database;
    }

    public String getUser () {
        return this.user;
    }

    public String getPassword () {
        return this.password;
    }

    public String getPrefix () {
        return this.prefix;
    }

    public void setPrefix (String prefix) {
        this.prefix = prefix;
    }

    public int getMaxPoolSize () {
        return this.maxPoolSize;
    }

    public String getJDBCUrl () {
        return "jdbc:mysql://" + this.getHost() + ":" + this.getPort() + "/" + this.getDatabase() + "?autoreconnect=true&serverTimezone=UTC&zeroDateTimeBehavior=convertToNull";
    }

    public int getPrepStmtCacheSize() {
        return prepStmtCacheSize;
    }

    public int getPrepStmtCacheSqlLimit() {
        return prepStmtCacheSqlLimit;
    }

}
