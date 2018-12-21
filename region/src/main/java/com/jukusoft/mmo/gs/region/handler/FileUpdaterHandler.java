package com.jukusoft.mmo.gs.region.handler;

import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.messages.DownloadRegionFilesRequest;
import com.jukusoft.mmo.gs.region.network.NetMessageHandler;
import com.jukusoft.mmo.gs.region.user.User;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;

public class FileUpdaterHandler implements NetMessageHandler<DownloadRegionFilesRequest> {

    protected final String LOG_TAG;

    public FileUpdaterHandler (String LOG_TAG) {
        this.LOG_TAG = LOG_TAG;
    }

    @Override
    public void receive(DownloadRegionFilesRequest msg, User user, int cid, RemoteConnection conn) {
        Log.v(LOG_TAG, "[FileUpdate] received region file request with " + msg.listRequestedFiles().size() + " requested files.");

        //TODO: add code here
    }

}
