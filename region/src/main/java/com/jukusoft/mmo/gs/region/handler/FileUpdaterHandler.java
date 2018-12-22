package com.jukusoft.mmo.gs.region.handler;

import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.memory.Pools;
import com.jukusoft.mmo.engine.shared.messages.DownloadRegionFileResponse;
import com.jukusoft.mmo.engine.shared.messages.DownloadRegionFilesRequest;
import com.jukusoft.mmo.gs.region.network.NetMessageHandler;
import com.jukusoft.mmo.gs.region.user.User;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;
import io.vertx.core.Vertx;

import java.io.File;

public class FileUpdaterHandler implements NetMessageHandler<DownloadRegionFilesRequest> {

    protected final String LOG_TAG;

    protected final long regionID;
    protected final int instanceID;

    protected final Vertx vertx;
    protected final String cachePath;

    public FileUpdaterHandler (String LOG_TAG, long regionID, int instanceID, Vertx vertx, String cachePath) {
        this.LOG_TAG = LOG_TAG;
        this.regionID = regionID;
        this.instanceID = instanceID;
        this.vertx = vertx;
        this.cachePath = cachePath + "client/";
    }

    @Override
    public void receive(DownloadRegionFilesRequest msg, User user, int cid, RemoteConnection conn) {
        Log.v(LOG_TAG, "[FileUpdate] received region file request with " + msg.listRequestedFiles().size() + " requested files.");

        for (String filePath : msg.listRequestedFiles()) {
            //for security reasons
            filePath = filePath.replace("..", ".");
            String path = this.cachePath + filePath;

            if (!new File(path).exists()) {
                Log.w(LOG_TAG, "Cannot find client region file: " + path);
                throw new IllegalStateException("client region file doesn't exists in ftp cache: " + path);
            }

            final String filePath1 = filePath;

            //read file and send content to client
            vertx.fileSystem().readFile(path, res -> {
                if (!res.succeeded()) {
                    Log.w(LOG_TAG, "Coulnd't read region file in cache: " + path);
                    return;
                }

                Log.v(LOG_TAG, "send file to client: " + filePath1 + " (cache path: " + path + ")");

                DownloadRegionFileResponse response = Pools.get(DownloadRegionFileResponse.class);
                response.regionID = this.regionID;
                response.instanceID = this.instanceID;
                response.filePath = filePath1;
                response.content = res.result();
                conn.send(response);
            });
        }
    }

}
