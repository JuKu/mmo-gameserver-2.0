package com.jukusoft.mmo.gs.region.subsystem.impl;

import com.jukusoft.mmo.gs.region.subsystem.SubSystemAdapter;
import com.jukusoft.mmo.gs.region.user.User;

public class CharacterDataService extends SubSystemAdapter {

    public void loadCharacter (User user, int cid) {
        //
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
