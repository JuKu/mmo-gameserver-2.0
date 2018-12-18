package com.jukusoft.mmo.gs.frontend.network;

import com.jukusoft.mmo.gs.region.RegionManager;
import com.jukusoft.vertx.connection.clientserver.CustomClientInitializer;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;
import com.jukusoft.vertx.connection.stream.BufferStream;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class ClientInitializer implements CustomClientInitializer {

    protected static final int EXPECTED_CONNECTIONS = 100;

    protected final RegionManager regionManager;
    protected final AtomicLong lastConnID = new AtomicLong(0);
    protected final LongObjectMap<ClientHandler> connections = new LongObjectHashMap<>(EXPECTED_CONNECTIONS);

    /**
     * default constructor
     *
     * @param regionManager singleton instance of region manager
     */
    public ClientInitializer(RegionManager regionManager) {
        Objects.requireNonNull(regionManager);
        this.regionManager = regionManager;
    }

    @Override
    public void handleConnect(BufferStream bufferStream, RemoteConnection conn) {
        //generate new local unique connection id
        final long connID = this.lastConnID.incrementAndGet();

        //create new client handler
        ClientHandler initializer = new ClientHandler(this.regionManager, connID, event -> {
            this.connections.remove(connID);
        });
        initializer.handleConnect(bufferStream, conn);

        //add initializer to map
        this.connections.put(connID, initializer);
    }

}
