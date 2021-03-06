package com.jukusoft.mmo.gs.region.ftp;

import com.jukusoft.mmo.engine.shared.logger.Log;
import io.github.bckfnn.ftp.FtpClient;
import io.vertx.core.Handler;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
* utils class to create directories or download files
 *
 * @deprecated because of introduction of <code>FtpUtil</code> class, use this class instead
*/
@Deprecated
public class FTPUtils {

    protected static final String LOG_TAG = "FTPUtils";

    //https://github.com/bckfnn/vertx-ftp-client/blob/3f33d076c10467908fd5f4591ebce809cae7cb48/src/main/java/io/github/bckfnn/ftp/FtpClient.java

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

    public static void listFiles (FtpClient ftpClient, String remoteDir, CountDownLatch latch, Handler<List<FTPFile>> handler) {
        Log.v(LOG_TAG, "try to list files from remote ftp server: " + remoteDir);
        System.err.println("try to list files from remote ftp server: " + remoteDir);

        ftpClient.list(remoteDir, res -> {
            if (!res.succeeded()) {
                Log.e(LOG_TAG, "Couldn't list directory: " + remoteDir, res.cause());
                handler.handle(null);
                latch.countDown();
                return;
            }

            String str = res.result().toString();

            List<FTPFile> files = FTPFile.listing(str);

            handler.handle(files);
            latch.countDown();
        });
    }

}
