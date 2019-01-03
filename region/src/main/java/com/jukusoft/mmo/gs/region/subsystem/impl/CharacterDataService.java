package com.jukusoft.mmo.gs.region.subsystem.impl;

import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.gs.region.subsystem.SubSystemAdapter;
import com.jukusoft.mmo.gs.region.user.User;

public class CharacterDataService extends SubSystemAdapter {

    protected static final String LOG_TAG = "CharDataService";

    public void loadCharacter (User user, int cid) {
        Log.d(LOG_TAG, "load character with cid " + cid + " (user: '" + user.getUsername() + "')...");

        //TODO: load character from database
    }

    @Override
    public boolean processCommand(User user, int cid, String cmd, String[] args) {
        return false;
    }

    @Override
    public void shutdown() {
        //
    }

}
