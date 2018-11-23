package com.jukusoft.mmo.gs.frontend.utils;

import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;

import java.io.File;
import java.io.IOException;

public class ConfigLoader {

    protected static final String CONFIG_TAG = "Config";

    protected ConfigLoader () {
        //
    }

    public static void load (String configDir, String[] args) throws IOException {
        //load all config files
        Log.i(CONFIG_TAG, "load configs in directory 'config/'...");
        Config.loadDir(new File(configDir));

        //overrides config with params
        for (String param : args) {
            if (param.startsWith("-Config:")) {
                param = param.substring(8);
                String[] array = param.split("=");

                if (array.length < 2) {
                    throw new IllegalArgumentException("invalide parameter, -Config parameters requires a '=' to set config value.");
                }

                String[] array1 = array[0].split("\\.");

                if (array1.length < 2) {
                    throw new IllegalArgumentException("invalide parameter, -Config parameters requires a '.' in option key to use section (current key: '" + array[0] + "').");
                }

                Log.d(CONFIG_TAG, "set value '" + array[0] + "' = '" + array[1] + "' manually.");

                //set config value
                Config.set(array1[0], array1[1], array[1]);
            }
        }
    }

}
