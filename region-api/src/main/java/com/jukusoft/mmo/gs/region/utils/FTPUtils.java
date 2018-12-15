package com.jukusoft.mmo.gs.region.utils;

import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import io.github.bckfnn.ftp.FtpClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class FTPUtils {

    protected static final String LOG_TAG = "FTPUtils";

    protected FTPUtils () {
        //
    }

    public static boolean mkdirSync (FtpClient client, String dir) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean b = new AtomicBoolean(false);

        client.mkd(dir, res -> {
            if (res.succeeded()) {
                b.set(true);
            } else {
                Log.w(LOG_TAG, "Couldn't create ftp directory on server: " + dir, res.cause());
            }

            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Log.w(LOG_TAG, "InterruptedException: ", e);
        }

        return b.get();
    }

    public static void downloadDir (FtpClient ftpClient, String remoteDir, String localDir, Handler<Boolean> handler) {
        //first, get a list with all files and directories on ftp server
        ftpClient.list(remoteDir, res -> {
            if (!res.succeeded()) {
                Log.e(LOG_TAG, "Couldn't list directory: " + remoteDir, res.cause());
                handler.handle(false);
                return;
            }

            String str = res.result().toString();
            str = str.replace("\r\n", "\n");
            String[] lines = str.split("\n");

            for (String line : lines) {
                System.err.println("line: " + line);
            }
        });
    }

}
