package com.jukusoft.mmo.gs.frontend;

import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.logger.LogWriter;
import com.jukusoft.mmo.engine.shared.utils.Utils;
import com.jukusoft.mmo.engine.shared.version.Version;
import com.jukusoft.mmo.gs.frontend.database.DatabaseFactory;
import com.jukusoft.mmo.gs.frontend.log.HzLogger;
import com.jukusoft.mmo.gs.frontend.utils.*;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;
import com.jukusoft.vertx.connection.clientserver.Server;
import com.jukusoft.vertx.connection.clientserver.TCPServer;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMain {

    protected static final String HAZELCAST_TAG = "Hazelcast";
    protected static final String SECTION_NAME = "GameServer";

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

        //get host (interface) and port from config
        String host = Config.get(SECTION_NAME, "host");
        int port = Config.getInt(SECTION_NAME, "port");

        //flag, if server started successfully
        AtomicBoolean b = new AtomicBoolean(false);

        //start tcp server
        TCPServer server = new TCPServer();
        server.init(vertx);
        server.setClientHandler(event -> {
            Log.i("TCPServer", "new client connection.");
        });

        //try to start tcp server
        server.start(host, port, event -> {
            if (event.succeeded()) {
                Log.i("Main", "tcp server started successfully!");
                b.set(true);
            } else {
                Log.w("Error", "Couldn't start tcp server... ", event.cause());
                shutdownLogs();
                System.exit(1);
            }
        });

        //TODO: add code here

        //wait while server is starting
        if (!b.get()) {
            Thread.sleep(100);
        }

        //inform others in cluster that this gs server exists
        IList<String> serverList = hazelcastInstance.getList("gs-servers-list");
        final String serverFingerprint = host + ":" + port + ":" + Version.getInstance().getVersion();
        serverList.add(serverFingerprint);

        //show console prompt and wait
        ConsoleWaiter.execute();

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
        shutdownLogs();

        //remove gs server from list
        serverList.remove(serverFingerprint);

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

    protected static void shutdownLogs () {
        //shutdown logger and write all remaining logs to file
        Log.shutdown();

        //wait 200ms, so logs can be written to file
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            //don't do anything here
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
