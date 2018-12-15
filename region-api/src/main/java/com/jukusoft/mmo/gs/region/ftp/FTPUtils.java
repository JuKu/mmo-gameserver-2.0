package com.jukusoft.mmo.gs.region.ftp;

import com.jukusoft.mmo.engine.shared.logger.Log;
import io.github.bckfnn.ftp.FtpClient;
import io.github.bckfnn.ftp.FtpFile;
import io.vertx.core.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FTPUtils {

    protected static final String LOG_TAG = "FTPUtils";

    private static Pattern filere = Pattern.compile("(.)......... +\\d+ +\\d+ +\\d+ +(\\d+) ([A-Z][a-z][a-z] +\\d+ +(?:(?:\\d\\d:\\d\\d)|(?:\\d+))) (.*)");

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

    public static void listFiles (FtpClient ftpClient, String remoteDir, CountDownLatch latch, Handler<List<FTPFile>> handler) {
        Log.v(LOG_TAG, "try to list files from remote ftp server: " + remoteDir);

        ftpClient.list(remoteDir, res -> {
            if (!res.succeeded()) {
                Log.e(LOG_TAG, "Couldn't list directory: " + remoteDir, res.cause());
                handler.handle(null);
                latch.countDown();
                return;
            }

            String str = res.result().toString();
            /*str = str.replace("\r\n", "\n");
            String[] lines = str.split("\n");

            for (String line : lines) {
                System.err.println(line);
                String[] array = line.split(" ");

                for (int i = 0; i < array.length; i++) {
                    System.err.println("[" + i + "] " + array[i]);
                }
            }*/

            List<FTPFile> files = FTPFile.listing(str);

            //quick and dirty fix, because the regex in FtpFile doesn't support the @ character for usernames, it only supports [A-Z][a-z][a-z].
            //str = str.replace("@", "AT");

            //List<FtpFile> files = FtpFile.listing(str);

            handler.handle(files);
            latch.countDown();
        });
    }

}
