package com.jukusoft.mmo.gs.region.ftp;

import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import io.github.bckfnn.ftp.FtpClient;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

@Deprecated
public class FTPFactory {

    protected static final String LOG_TAG = "FTP";
    protected static final String CONFIG_SECTION = "FTP";

    protected static Vertx vertx = null;

    protected FTPFactory () {
        //
    }

    public static void init (Vertx vertx) {
        Objects.requireNonNull(vertx);
        FTPFactory.vertx = vertx;
    }

    public static void create (String host, int port, String user, String password, CountDownLatch latch, Handler<FtpClient> handler) {
        if (vertx == null) {
            throw new IllegalStateException("initialize FTPFactory with FTPFactory.init() call first!");
        }

        FtpClient client = new FtpClient(vertx, host, port);
        client.connect(connectRes -> {
            if (!connectRes.succeeded()) {
                System.err.println("Coulnd't connect to ftp server: " + connectRes.cause());
                connectRes.cause().printStackTrace();
                Log.w(LOG_TAG, "Coulnd't connect to ftp server: ", connectRes.cause());

                handler.handle(null);
                latch.countDown();
                return;
            }

            client.login(user, password, loginRes -> {
                if (!loginRes.succeeded()) {
                    System.err.println("Coulnd't login on ftp server: " + connectRes.cause());

                    if (loginRes.cause() != null) {
                        loginRes.cause().printStackTrace();
                    }

                    Log.w(LOG_TAG, "Coulnd't login on ftp server: ", connectRes.cause());
                    handler.handle(null);
                    latch.countDown();
                    return;
                }

                handler.handle(client);
                latch.countDown();
            });
        });
    }

    public static void create (CountDownLatch latch, Handler<FtpClient> handler) {
        create(Config.get(CONFIG_SECTION, "host"), Config.getInt(CONFIG_SECTION, "port"), Config.get(CONFIG_SECTION, "username"), Config.get(CONFIG_SECTION, "password"), latch, handler);
    }

    public static void createAsync (Handler<FtpClient> handler) {
        CountDownLatch latch = new CountDownLatch(1);
        create(Config.get(CONFIG_SECTION, "host"), Config.getInt(CONFIG_SECTION, "port"), Config.get(CONFIG_SECTION, "username"), Config.get(CONFIG_SECTION, "password"), latch, handler);
    }

    public static FtpClient createSync () {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<FtpClient> ar = new AtomicReference<>();

        create(Config.get(CONFIG_SECTION, "host"), Config.getInt(CONFIG_SECTION, "port"), Config.get(CONFIG_SECTION, "username"), Config.get(CONFIG_SECTION, "password"), latch, event -> ar.set(event));

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.w(LOG_TAG, "InterruptedException: ", e);
        }

        if (ar.get() == null) {
            throw new IllegalStateException("Coulnd't create ftp client.");
        }

        return ar.get();
    }

}
