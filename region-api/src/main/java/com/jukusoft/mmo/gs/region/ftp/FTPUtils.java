package com.jukusoft.mmo.gs.region.ftp;

import com.jukusoft.mmo.engine.shared.logger.Log;
import io.github.bckfnn.ftp.FtpClient;
import io.vertx.core.Handler;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
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
        //

        handler.handle(true);
    }

    /*public static void recursiveListFiles (FtpClient ftpClient, String remoteDir, CountDownLatch latch, Handler<List<String>> handler) throws InterruptedException {
        recursiveListFiles(ftpClient, remoteDir, latch, new ArrayList<>(), new ArrayList<>(), "", event -> {
            //
        });
    }*/

    /*public static void recursiveListFiles (FtpClient ftpClient, String remoteDir, CountDownLatch latch, List<String> dirList, List<String> fileList, String prefix, Handler<Boolean> handler) throws InterruptedException {
        if (!remoteDir.endsWith("/")) {
            throw new IllegalArgumentException("remoteDir has to end with '/'!");
        }

        System.err.println("listDir: " + remoteDir);

        CountDownLatch latch1 = new CountDownLatch(1);

        listFiles(ftpClient, remoteDir, latch1, list -> {
            System.err.println("dir '" + remoteDir + "' contains " + list.size() + " entries.");

            for (FTPFile file : list) {
                System.err.println("FTPFile: " + file.getName());

                if (file.isDirectory()) {
                    dirList.add(prefix + file.getName());

                    CountDownLatch latch2 = new CountDownLatch(1);

                    System.err.println("isDir: " + prefix + file.getName());

                    //TODO: search for other dirs
                    try {
                        recursiveListFiles(ftpClient, remoteDir + file.getName() + "/", latch2, dirList, fileList, prefix + file.getName() + "/", event -> {
                            //latch2.countDown();
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.w(LOG_TAG, "InterruptedException in method FTPUtils.recursiveListFiles(): ", e);

                        latch2.countDown();
                    }

                    try {
                        latch2.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.w(LOG_TAG, "InterruptedException in method FTPUtils.recursiveListFiles(): ", e);
                    }
                } else {
                    System.err.println("file: " + prefix + file.getName());
                    fileList.add(prefix + file.getName());
                }
            }

            latch1.countDown();
        });

        latch1.await();

        handler.handle(true);
        latch.countDown();
    }*/

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
