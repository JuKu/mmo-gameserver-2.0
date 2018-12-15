package com.jukusoft.mmo.gs.frontend.utils;

import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import io.github.bckfnn.ftp.FtpClient;

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

}
