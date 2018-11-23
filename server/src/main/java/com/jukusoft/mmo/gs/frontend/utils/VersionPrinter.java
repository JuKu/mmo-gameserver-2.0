package com.jukusoft.mmo.gs.frontend.utils;

import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.utils.Utils;
import com.jukusoft.mmo.engine.shared.version.Version;
import com.jukusoft.mmo.gs.frontend.ServerMain;

public class VersionPrinter {

    protected static final String VERSION_TAG = "Version";

    protected VersionPrinter () {
        //
    }

    public static void print () {
        //set global version
        Version version = new Version(ServerMain.class);
        Version.setInstance(new Version(ServerMain.class));

        //print proxy server version information
        Utils.printSection("Proxy Version");
        Log.i(VERSION_TAG, "Version: " + version.getVersion());
        Log.i(VERSION_TAG, "Build: " + version.getRevision());
        Log.i(VERSION_TAG, "Build JDK: " + version.getBuildJdk());
        Log.i(VERSION_TAG, "Build Time: " + version.getBuildTime());
        Log.i(VERSION_TAG, "Vendor ID: " + (!version.getVendor().equals("n/a") ? version.getVendor() : version.getVendorID()));

        //print java version
        Utils.printSection("Java Version");
        Log.i("Java", "Java Vendor: " + System.getProperty("java.vendor"));
        Log.i("Java", "Java Vendor URL: " + System.getProperty("java.vendor.url"));
        Log.i("Java", "Java Version: " + System.getProperty("java.version"));
    }

}
