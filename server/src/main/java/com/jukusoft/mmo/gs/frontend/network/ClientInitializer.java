package com.jukusoft.mmo.gs.frontend.network;

import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.messages.JoinRegionMessage;
import com.jukusoft.mmo.gs.region.RegionContainer;
import com.jukusoft.mmo.gs.region.RegionManager;
import com.jukusoft.mmo.gs.region.user.User;
import com.jukusoft.vertx.connection.clientserver.CustomClientInitializer;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;
import com.jukusoft.vertx.connection.stream.BufferStream;
import com.jukusoft.vertx.serializer.Serializer;
import com.jukusoft.vertx.serializer.utils.ByteUtils;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;

import java.util.Objects;

public class ClientInitializer implements CustomClientInitializer {

    protected static final String LOG_TAG = "ClientInit";
    protected static final String AUTH_TAG = "Auth";

    //authentification state (if proxy has send cluster password to check if it is allowed to connect to gameserver)
    protected boolean authentificated = false;

    protected RemoteConnection conn = null;
    protected ConnState state = new ConnState();

    protected final RegionManager regionManager;

    protected RegionContainer regionContainer = null;

    /**
    * default constructor
     *
     * @param regionManager singleton instance of region manager
    */
    public ClientInitializer (RegionManager regionManager) {
        Objects.requireNonNull(regionManager);
        this.regionManager = regionManager;
    }

    @Override
    public void handleConnect(BufferStream bufferStream, RemoteConnection conn) {
        Objects.requireNonNull(bufferStream);
        Objects.requireNonNull(conn);

        Log.v(LOG_TAG, "handleConnect()");

        this.conn = conn;

        //set message handler
        bufferStream.handler(buffer -> this.onMessage(buffer, conn));

        bufferStream.endHandler(v -> this.onClose(conn));
    }

    protected void onClose (RemoteConnection conn) {
        Log.i(LOG_TAG, "proxy connection closed: " + conn.remoteHost() + ":" + conn.remotePort());
    }

    protected void onMessage (Buffer buffer, RemoteConnection conn) {
        Log.v(LOG_TAG, "message from proxy server received with type " + ByteUtils.byteToHex(buffer.getByte(0)) + ", extendedType: " + ByteUtils.byteToHex(buffer.getByte(1)) + ".");

        byte type = buffer.getByte(0);
        byte extendedType = buffer.getByte(1);

        //check, if user is authentificated
        if (!authentificated) {
            //check, if message is a join message
            if (type == 0x01 && extendedType == 0x07) {
                //pass message

                Log.i(LOG_TAG, "auth (join) message from proxy server received.");

                //handle message
                JoinRegionMessage joinMessage = Serializer.unserialize(buffer);

                //check cluster credentials
                if (joinMessage.cluster_username.equals(Config.get("Cluster", "username")) && joinMessage.cluster_password.equals(Config.get("Cluster", "password"))) {
                    //cluster credentials right

                    //set user state
                    state.setAuthorized(joinMessage.userID, joinMessage.username, joinMessage.cid, joinMessage.listGroups());
                    state.setRegion(joinMessage.regionID, joinMessage.instanceID, joinMessage.cid);
                    Log.i(AUTH_TAG, "proxy connection (" + conn.remoteHost() + ":" + conn.remotePort() + ") authentificated successfully!");

                    //find region to redirect future messages
                    this.regionContainer = this.regionManager.find(joinMessage.regionID, joinMessage.instanceID, joinMessage.shardID);

                    if (this.regionContainer == null) {
                        Log.w(LOG_TAG, "region (" + joinMessage.regionID + "-" + joinMessage.instanceID + "-" + joinMessage.shardID + ") doesn't run on this server!");

                        //TODO: send error message to client

                        //TODO: maybe, remove this line later
                        //close connection
                        this.conn.disconnect();

                        return;
                    }

                    //initialize player on container
                    User user = new User(joinMessage.userID, joinMessage.username, joinMessage.listGroups());
                    this.regionContainer.initPlayer(user, joinMessage.cid);

                    this.authentificated = true;

                    //TODO: send response to proxy / client
                } else {
                    Log.w(AUTH_TAG, "cluster credentials are wrong for username '" + joinMessage.cluster_username + "', close connection now.");
                    conn.disconnect();

                    return;
                }
            } else {
                //drop message, because proxy isn't authentificated
                Log.w(LOG_TAG, "Drop message with type " + type + ", extendedType: " + extendedType + " because proxy isn't authentificated yet.");

                return;
            }
        } else {
            //redirect message to region
            this.regionContainer.receive(buffer, conn);
        }
    }

}
