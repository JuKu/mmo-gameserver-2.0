package com.jukusoft.mmo.gs.frontend;

import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.logger.LogWriter;
import com.jukusoft.mmo.engine.shared.utils.Utils;
import com.jukusoft.mmo.gs.frontend.database.DatabaseFactory;
import com.jukusoft.mmo.gs.frontend.log.HzLogger;
import com.jukusoft.mmo.gs.frontend.utils.ConfigLoader;
import com.jukusoft.mmo.gs.frontend.utils.HazelcastFactory;
import com.jukusoft.mmo.gs.frontend.utils.VersionPrinter;
import com.jukusoft.mmo.gs.frontend.utils.VertxManager;
import io.vertx.core.Vertx;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMain {

    protected static final String HAZELCAST_TAG = "Hazelcast";

    public static void main (String[] args) {
        try {
            start(args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    protected static void start (String[] args) throws Exception {
        //load logger config
        try {
            Config.load(new File("./config/logger.cfg"));

            //initialize logger
            Log.init();
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Couldn't initialize config and logger!", e);
            System.exit(0);
        }

        Log.i("Startup", "Start game server.");

        //print version
        VersionPrinter.print();

        //load config
        Utils.printSection("Configuration & Init");
        ConfigLoader.load("./config/", args);

        //initialize hazelcast
        Log.i(HAZELCAST_TAG, "create new hazelcast instance...");
        HazelcastInstance hazelcastInstance = createHazelcastInstance();
        Log.i(HAZELCAST_TAG, "hazelcast started successfully.");

        //create and attach hazelcast logger
        Log.i("Logging", "enable hazelcast cluster logging...");
        HzLogger hzLogger = new HzLogger(hazelcastInstance);
        LogWriter.attachListener(hzLogger);

        //initialize database connection
        DatabaseFactory.build();

        //create vert.x instance
        Log.i("Vertx", "Create vertx.io instance...");
        VertxManager vertxManager = new VertxManager();
        vertxManager.init(hazelcastInstance);
        Vertx vertx = vertxManager.getVertx();

        //TODO: add code here

        /**
         * shutdown process
         */

        //list currently active threads
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

        //print log
        Utils.printSection("Shutdown");
        Log.i("Shutdown", "Shutdown now.");

        //shutdown vertx
        vertxManager.shutdown();

        //shutdown logger and write all remaining logs to file
        Log.shutdown();

        //wait 200ms, so logs can be written to file
        Thread.sleep(500);

        //check, if there are other active threads, except the main thread
        if (threadSet.size() > 1) {
            System.err.println("Shutdown: waiting for active threads:");

            for (Thread thread : threadSet) {
                System.err.println(" - " + thread.getName());
            }

            //wait 3 seconds, then force shutdown
            Thread.sleep(2000);
        }

        System.err.println("shutdown JVM now.");

        //force JVM shutdown
        if (Config.forceExit) {
            System.exit(0);
        }
    }

    protected static HazelcastInstance createHazelcastInstance () {
        if (Config.getBool(HAZELCAST_TAG, "standalone")) {
            //create an new hazelcast instance
            com.hazelcast.config.Config config = new com.hazelcast.config.Config();

            //disable hazelcast logging
            config.setProperty("hazelcast.logging.type", "none");

            CacheSimpleConfig cacheConfig = new CacheSimpleConfig();
            config.getCacheConfigs().put("session-cache", cacheConfig);

            return Hazelcast.newHazelcastInstance(config);
        } else {
            return HazelcastFactory.createHzInstanceFromConfig();
        }
    }

}
