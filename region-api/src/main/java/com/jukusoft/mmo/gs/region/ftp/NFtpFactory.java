package com.jukusoft.mmo.gs.region.ftp;

import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.apache.commons.net.ftp.FTPClient;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class NFtpFactory {

    protected static final String LOG_TAG = "NFtpFactory";
    protected static final String CONFIG_SECTION = "FTP";

    protected static Vertx vertx = null;

    protected NFtpFactory () {
        //
    }

    public static void init (Vertx vertx) {
        Objects.requireNonNull(vertx);
        NFtpFactory.vertx = vertx;
    }

    public static void create (String host, int port, String user, String password, CountDownLatch latch, Handler<FTPClient> handler) {
        if (vertx == null) {
            throw new IllegalStateException("initialize NFtpFactory with NFtpFactory.init() call first!");
        }

        //https://www.rhymewithgravy.com/2016/10/18/Vertx-and-Blocking-Code.html
        vertx.executeBlocking((Handler<Future<FTPClient>>) future -> {
            try {
                FTPClient ftpClient = new FTPClient();

                // connect and login to the server
                ftpClient.connect(host, port);
                ftpClient.login(user, password);

                future.complete(ftpClient);
            } catch (Exception e) {
                Log.w(LOG_TAG, "Exception while connecting and login to ftp server: ", e);

                future.fail(e);
            }
        }, res -> {
            if (res.succeeded()) {
                handler.handle(res.result());
            } else {
                handler.handle(null);
            }

            latch.countDown();
        });
    }

    public static void createAsync (Handler<FTPClient> handler) {
        CountDownLatch latch = new CountDownLatch(1);
        create(Config.get(CONFIG_SECTION, "host"), Config.getInt(CONFIG_SECTION, "port"), Config.get(CONFIG_SECTION, "username"), Config.get(CONFIG_SECTION, "password"), latch, handler);
    }

    public static FTPClient createSync () {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<FTPClient> ar = new AtomicReference<>();

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
