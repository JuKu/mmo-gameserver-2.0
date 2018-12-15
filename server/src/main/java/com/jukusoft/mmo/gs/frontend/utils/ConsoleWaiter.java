package com.jukusoft.mmo.gs.frontend.utils;

import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.utils.Utils;
import com.jukusoft.mmo.gs.region.ftp.FTPFactory;
import com.jukusoft.mmo.gs.region.utils.FTPUtils;
import io.github.bckfnn.ftp.FtpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleWaiter {

    protected static final String LOG_TAG = "CLI";

    protected ConsoleWaiter () {
        //
    }

    public static void execute () throws IOException {
        Utils.printSection("Running");

        //wait
        Thread thread = Thread.currentThread();
        thread.setName("main");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        Log.i(LOG_TAG, "command line input is accepted yet. Quit server with 'quit' and ENTER.");

        while (!Thread.interrupted()) {
            //read line
            String line = reader.readLine();

            if (line.equals("quit") || line.equals("exit")) {
                break;
            } else if (line.startsWith("/createFTPRegion")) {
                String[] args = line.split(" ");

                if (args.length < 2) {
                    Log.w(LOG_TAG, "region id is required! Correct usage: /createFTPRegion <ID>");
                    continue;
                }

                long regionID = Long.parseLong(args[1]);
                Log.i(LOG_TAG, "try to create region " + regionID + "...");

                //open ftp connection
                FtpClient ftp = FTPFactory.createSync();

                String dir = Config.get("FTP", "regionsDir") + "/region_" + regionID + "_1";
                boolean b1 = FTPUtils.mkdirSync(ftp, dir);
                boolean b2 = FTPUtils.mkdirSync(ftp, dir + "/client");
                boolean b3 = FTPUtils.mkdirSync(ftp, dir + "/server");

                if (b1 && b2 && b3) {
                    Log.i(LOG_TAG, "created region with regionID " + regionID + " on ftp server successfully!");
                } else {
                    Log.w(LOG_TAG, "Couldn't create region with regionID " + regionID + " on ftp server!");
                }

                ftp.quit(event -> {
                    //don't do anything here
                });
            }

            System.out.println("Unsupported command: " + line);
        }
    }

}
