package com.jukusoft.mmo.gs.frontend.network;

import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.messages.JoinRegionMessage;
import com.jukusoft.mmo.gs.region.RegionContainer;
import com.jukusoft.mmo.gs.region.RegionManager;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;
import com.jukusoft.vertx.connection.stream.BufferStream;
import com.jukusoft.vertx.serializer.Serializer;
import com.jukusoft.vertx.serializer.TypeLookup;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class ClientInitializerTest {

    @BeforeClass
    public static void beforeClass () {
        TypeLookup.register(JoinRegionMessage.class);

        Config.set("Cluster", "username", "dev");
        Config.set("Cluster", "password", "dev-pass");
    }

    @AfterClass
    public static void afterClass () {
        Config.clear();
    }

    @Test (expected = NullPointerException.class)
    public void testNullConstructor () {
        new ClientInitializer(null);
    }

    @Test
    public void testConstructor () {
        new ClientInitializer(new RegionManager() {
            @Override
            public RegionContainer find(long regionID, int instanceID, int shardID) {
                return null;
            }

            @Override
            public void start(long regionID, int instanceID, int shardID, Handler<RegionContainer> handler) {
                //
            }
        });
    }

    @Test
    public void testHandleConnect () {
        ClientInitializer initializer = new ClientInitializer(Mockito.mock(RegionManager.class));

        BufferStream stream = new BufferStream(createReadStream(), createWriteStream());
        initializer.handleConnect(stream, Mockito.mock(RemoteConnection.class));
    }

    @Test (expected = UnauthentificatedException.class)
    public void testOnUnauthentificatedMessage () {
        ClientInitializer initializer = new ClientInitializer(Mockito.mock(RegionManager.class));

        //test internal state
        assertEquals(false, initializer.authentificated);

        //create example message
        Buffer buffer = Buffer.buffer();
        buffer.appendByte((byte) 0x01);
        buffer.appendByte((byte) 0x02);

        initializer.onMessage(buffer, Mockito.mock(RemoteConnection.class));
    }

    @Test (expected = WrongClusterCredentialsException.class)
    public void testHandleJoinMessageWithWrongCredentials () {
        ClientInitializer initializer = new ClientInitializer(Mockito.mock(RegionManager.class));

        JoinRegionMessage msg = new JoinRegionMessage();
        msg.cluster_username = "test";
        msg.cluster_password = "testpass";
        Buffer buffer = Serializer.serialize(msg);

        initializer.onMessage(buffer, Mockito.mock(RemoteConnection.class));
    }

    @Test
    public void testOnClose () {
        ClientInitializer initializer = new ClientInitializer(Mockito.mock(RegionManager.class));
        initializer.onClose(Mockito.mock(RemoteConnection.class));
    }

    private ReadStream<Buffer> createReadStream () {
        //create a dummy object which directly calls handler
        return new ReadStream<Buffer>() {
            @Override
            public ReadStream<Buffer> exceptionHandler(Handler<Throwable> handler) {
                handler.handle(new Throwable("test"));
                return this;
            }

            @Override
            public ReadStream<Buffer> handler(Handler<Buffer> handler) {
                handler.handle(Buffer.buffer());
                return this;
            }

            @Override
            public ReadStream<Buffer> pause() {
                return this;
            }

            @Override
            public ReadStream<Buffer> resume() {
                return this;
            }

            @Override
            public ReadStream<Buffer> endHandler(Handler<Void> endHandler) {
                endHandler.handle(null);
                return this;
            }
        };
    }

    private WriteStream<Buffer> createWriteStream () {
        return new WriteStream<Buffer>() {
            @Override
            public WriteStream<Buffer> exceptionHandler(Handler<Throwable> handler) {
                handler.handle(new Throwable("test"));
                return this;
            }

            @Override
            public WriteStream<Buffer> write(Buffer data) {
                return this;
            }

            @Override
            public void end() {
                //
            }

            @Override
            public WriteStream<Buffer> setWriteQueueMaxSize(int maxSize) {
                return this;
            }

            @Override
            public boolean writeQueueFull() {
                return false;
            }

            @Override
            public WriteStream<Buffer> drainHandler(Handler<Void> handler) {
                handler.handle(null);
                return this;
            }
        };
    }

}
