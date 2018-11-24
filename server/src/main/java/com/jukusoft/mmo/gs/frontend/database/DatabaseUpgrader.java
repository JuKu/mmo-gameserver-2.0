package com.jukusoft.mmo.gs.frontend.database;

import com.jukusoft.mmo.engine.shared.logger.Log;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;

import java.util.HashMap;
import java.util.Map;

public class DatabaseUpgrader {

    protected Flyway flyway = null;

    public DatabaseUpgrader(MySQLConfig mySQLConfig) {
        //create the Flyway instance
        this.flyway = new Flyway();

        //https://github.com/timander/flyway-example

        //https://scalified.com/2018/01/17/java-backend-database-migration-flyway/

        //https://github.com/timander/flyway-example/blob/master/flyway.conf

        //https://www.programcreek.com/java-api-examples/index.php?api=com.googlecode.flyway.core.Flyway

        //http://www.liquibase.org/

        //https://flywaydb.org/documentation/migrations

        this.flyway.setDataSource("jdbc:mysql://" + mySQLConfig.getHost() + ":" + mySQLConfig.getPort() + "/" + mySQLConfig.getDatabase() + "?autoreconnect=true&serverTimezone=UTC&zeroDateTimeBehavior=convertToNull", mySQLConfig.getUser(), mySQLConfig.getPassword());

        Map<String,String> placeholderMap = new HashMap<>();
        placeholderMap.put("prefix", "");

        //set table prefix
        if (!mySQLConfig.getPrefix().isEmpty()) {
            placeholderMap.put("prefix", mySQLConfig.getPrefix());
        }

        this.flyway.setPlaceholders(placeholderMap);

        //set location
        this.flyway.setLocations(mySQLConfig.getFlywayLocation());

        //set encoding
        this.flyway.setEncoding("utf-8");
    }

    public void migrate () {
        for (String loc : this.flyway.getLocations()) {
            Log.v("Flyway", "flyway location: " + loc);
        }

        //create or upgrade database schema
        this.flyway.migrate();

        this.flyway.validate();
    }

    public void printInfo (String tag) {
        MigrationInfoService infoService = this.flyway.info();

        for (MigrationInfo info : infoService.all()) {
            Log.i(tag, " - " + info.getDescription() + ", script: " + info.getScript() + ", state: " + info.getState() + ", version: " + info.getVersion());
        }
    }

}
