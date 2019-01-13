package com.jukusoft.mmo.gs.region.subsystem.impl;

import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.memory.Pools;
import com.jukusoft.mmo.engine.shared.messages.play.ClientReadyToPlayRequest;
import com.jukusoft.mmo.engine.shared.messages.play.ClientReadyToPlayResponse;
import com.jukusoft.mmo.gs.region.subsystem.InjectSubSystem;
import com.jukusoft.mmo.gs.region.subsystem.SubSystemAdapter;
import com.jukusoft.mmo.gs.region.subsystem.impl.utils.NetHandlersProxy;
import com.jukusoft.mmo.gs.region.user.User;

public class CharacterDataService extends SubSystemAdapter {

    protected static final String LOG_TAG = "CharDataService";

    @InjectSubSystem (nullable = false)
    protected NetHandlersProxy netHandlers;

    @Override
    protected void onInit() {
        netHandlers.register(ClientReadyToPlayRequest.class, (msg, user, cid, conn) -> {
            Log.i(LOG_TAG, "client (userID: " + user.getUserID() + ", username: " + user.getUsername() + ", cid: " + cid + ") is ready to play.");

            //TODO: inform other subsystems and remove hide flag from player

            //send response to client
            ClientReadyToPlayResponse response = Pools.get(ClientReadyToPlayResponse.class);
            conn.send(response);
        });
    }

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
