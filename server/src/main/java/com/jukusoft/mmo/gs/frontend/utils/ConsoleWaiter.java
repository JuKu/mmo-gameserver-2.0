package com.jukusoft.mmo.gs.frontend.utils;

import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleWaiter {

    protected ConsoleWaiter () {
        //
    }

    public static void execute () throws IOException {
        Utils.printSection("Running");

        //wait
        Thread thread = Thread.currentThread();
        thread.setName("main");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        Log.i("CLI", "command line input is accepted yet. Quit server with 'quit' and ENTER.");

        while (!Thread.interrupted()) {
            //read line
            String line = reader.readLine();

            if (line.equals("quit") || line.equals("exit")) {
                break;
            }

            System.out.println("Unsupported command: " + line);
        }
    }

}
