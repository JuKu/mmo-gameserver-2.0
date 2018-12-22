package com.jukusoft.mmo.gs.frontend.network;

import com.jukusoft.mmo.engine.shared.config.Cache;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.messages.JoinRegionMessage;
import com.jukusoft.mmo.gs.region.RegionContainer;
import com.jukusoft.mmo.gs.region.RegionContainerImpl;
import com.jukusoft.mmo.gs.region.RegionManager;
import com.jukusoft.mmo.gs.region.user.User;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;
import com.jukusoft.vertx.connection.stream.BufferStream;
import com.jukusoft.vertx.serializer.Serializer;
import com.jukusoft.vertx.serializer.TypeLookup;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;

public class ClientHandlerTest {

    @BeforeClass
    public static void beforeClass () {
        TypeLookup.register(JoinRegionMessage.class);

        Config.set("Cluster", "username", "dev");
        Config.set("Cluster", "password", "dev-pass");

        Cache.init("../cache/");
    }

    @AfterClass
    public static void afterClass () {
        Config.clear();
    }

    @Test (expected = NullPointerException.class)
    public void testNullConstructor () {
        new ClientHandler(null, 1, Mockito.mock(Handler.class));
    }

    @Test
    public void testConstructor () {
        new ClientHandler(new RegionManager() {
            @Override
            public RegionContainer find(long regionID, int instanceID, int shardID) {
                return null;
            }

            @Override
            public void start(long regionID, int instanceID, int shardID, Handler<RegionContainer> handler) {
                //
            }
        }, 1, Mockito.mock(Handler.class));
    }

    @Test
    public void testHandleConnect () {
        ClientHandler initializer = new ClientHandler(Mockito.mock(RegionManager.class), 1, Mockito.mock(Handler.class));

        BufferStream stream = new BufferStream(createReadStream(), createWriteStream());
        initializer.handleConnect(stream, Mockito.mock(RemoteConnection.class));
    }

    @Test (expected = UnauthentificatedException.class)
    public void testOnUnauthentificatedMessage () {
        ClientHandler initializer = new ClientHandler(Mockito.mock(RegionManager.class), 1, Mockito.mock(Handler.class));

        //test internal state
        assertEquals(false, initializer.authentificated);

        //create example message
        Buffer buffer = Buffer.buffer();
        buffer.appendByte((byte) 0x01);
        buffer.appendByte((byte) 0x02);

        initializer.onMessage(buffer, Mockito.mock(RemoteConnection.class));
    }

    @Test (expected = UnauthentificatedException.class)
    public void testOnUnauthentificatedMessage1 () {
        ClientHandler initializer = new ClientHandler(Mockito.mock(RegionManager.class), 1, Mockito.mock(Handler.class));

        //test internal state
        assertEquals(false, initializer.authentificated);

        //create example message
        Buffer buffer = Buffer.buffer();
        buffer.appendByte((byte) 0x02);
        buffer.appendByte((byte) 0x03);

        initializer.onMessage(buffer, Mockito.mock(RemoteConnection.class));
    }

    @Test (expected = UnauthentificatedException.class)
    public void testOnUnauthentificatedMessage2 () {
        ClientHandler initializer = new ClientHandler(Mockito.mock(RegionManager.class), 1, Mockito.mock(Handler.class));

        //test internal state
        assertEquals(false, initializer.authentificated);

        //create example message
        Buffer buffer = Buffer.buffer();
        buffer.appendByte((byte) 0x02);
        buffer.appendByte((byte) 0x07);

        initializer.onMessage(buffer, Mockito.mock(RemoteConnection.class));
    }

    @Test
    public void testOnMessageRedirect () throws IOException {
        Config.load(new File("../config/junit-logger.cfg"));
        Log.init();

        ClientHandler initializer = new ClientHandler(Mockito.mock(RegionManager.class), 1, Mockito.mock(Handler.class));
        initializer.authentificated = true;
        initializer.user = new User(1, "test", new ArrayList<>());

        AtomicBoolean b = new AtomicBoolean(false);

        initializer.regionContainer = Mockito.mock(RegionContainer.class);
        Mockito.when(initializer.regionContainer.isInitialized()).thenReturn(true);
        Mockito.doAnswer(invocation -> {
            System.err.println("handler called.");
            b.set(true);
            return null;
        }).when(initializer.regionContainer).receive(any(Buffer.class), any(User.class), anyInt(), any(RemoteConnection.class));

        //create example message
        Buffer buffer = Buffer.buffer();
        buffer.appendByte((byte) 0x01);
        buffer.appendByte((byte) 0x02);

        assertEquals(false, b.get());

        initializer.onMessage(buffer, Mockito.mock(RemoteConnection.class));

        Log.shutdown();

        //check, if receive() was called, this means message was redirected
        assertEquals(true, b.get());
    }

    @Test (expected = RuntimeException.class)
    public void testOnMessageRedirectWithException () {
        ClientHandler initializer = new ClientHandler(Mockito.mock(RegionManager.class), 1, Mockito.mock(Handler.class));
        initializer.authentificated = true;
        initializer.user = new User(1, "test", new ArrayList<>());

        initializer.regionContainer = Mockito.mock(RegionContainer.class);
        Mockito.when(initializer.regionContainer.isInitialized()).thenReturn(true);
        Mockito.doAnswer(invocation -> {
            throw new IllegalStateException("test exception");
        }).when(initializer.regionContainer).receive(any(Buffer.class), any(User.class), anyInt(), any(RemoteConnection.class));

        //create example message
        Buffer buffer = Buffer.buffer();
        buffer.appendByte((byte) 0x01);
        buffer.appendByte((byte) 0x02);

        initializer.onMessage(buffer, Mockito.mock(RemoteConnection.class));
    }

    @Test (expected = WrongClusterCredentialsException.class)
    public void testHandleJoinMessageWithWrongCredentials () {
        ClientHandler initializer = new ClientHandler(Mockito.mock(RegionManager.class), 1, Mockito.mock(Handler.class));

        JoinRegionMessage msg = new JoinRegionMessage();
        msg.cluster_username = "test";
        msg.cluster_password = "testpass";
        Buffer buffer = Serializer.serialize(msg);

        initializer.onMessage(buffer, Mockito.mock(RemoteConnection.class));
    }

    @Test (expected = WrongClusterCredentialsException.class)
    public void testHandleJoinMessageWithWrongClusterUser () {
        ClientHandler initializer = new ClientHandler(Mockito.mock(RegionManager.class), 1, Mockito.mock(Handler.class));

        JoinRegionMessage msg = new JoinRegionMessage();
        msg.cluster_username = "test";
        msg.cluster_password = "dev-pass";
        Buffer buffer = Serializer.serialize(msg);

        initializer.onMessage(buffer, Mockito.mock(RemoteConnection.class));
    }

    @Test (expected = WrongClusterCredentialsException.class)
    public void testHandleJoinMessageWithWrongClusterPassword () {
        ClientHandler initializer = new ClientHandler(Mockito.mock(RegionManager.class), 1, Mockito.mock(Handler.class));

        JoinRegionMessage msg = new JoinRegionMessage();
        msg.cluster_username = "dev";
        msg.cluster_password = "testpass";
        Buffer buffer = Serializer.serialize(msg);

        initializer.onMessage(buffer, Mockito.mock(RemoteConnection.class));
    }

    @Test
    public void testHandleJoinMessageWithCorrectCredentials () {
        RegionManager regionManager = Mockito.mock(RegionManager.class);
        Mockito.when(regionManager.find(anyLong(), anyInt(), anyInt())).thenReturn(new RegionContainerImpl(Mockito.mock(Vertx.class), 1, 2, 3));

        ClientHandler initializer = new ClientHandler(regionManager, 1, Mockito.mock(Handler.class));

        JoinRegionMessage msg = new JoinRegionMessage();
        msg.cluster_username = "dev";
        msg.cluster_password = "dev-pass";
        msg.userID = 1;
        msg.username = "testuser";
        msg.setGroups(new ArrayList<>());
        Buffer buffer = Serializer.serialize(msg);

        initializer.onMessage(buffer, Mockito.mock(RemoteConnection.class));
    }

    @Test (expected = IllegalStateException.class)
    public void testHandleJoinMessageWithCorrectCredentialsNullContainer () {
        RegionManager regionManager = Mockito.mock(RegionManager.class);
        Mockito.when(regionManager.find(anyLong(), anyInt(), anyInt())).thenReturn(null);

        ClientHandler initializer = new ClientHandler(regionManager, 1, Mockito.mock(Handler.class));
        initializer.conn = Mockito.mock(RemoteConnection.class);

        JoinRegionMessage msg = new JoinRegionMessage();
        msg.cluster_username = "dev";
        msg.cluster_password = "dev-pass";
        msg.userID = 1;
        msg.username = "testuser";
        msg.setGroups(new ArrayList<>());
        Buffer buffer = Serializer.serialize(msg);

        initializer.onMessage(buffer, Mockito.mock(RemoteConnection.class));
    }

    @Test (expected = IllegalStateException.class)
    public void testHandleJoinMessageWithCorrectCredentialsRegionNotRunning () {
        ClientHandler initializer = new ClientHandler(Mockito.mock(RegionManager.class), 1, Mockito.mock(Handler.class));

        JoinRegionMessage msg = new JoinRegionMessage();
        msg.cluster_username = "dev";
        msg.cluster_password = "dev-pass";
        Buffer buffer = Serializer.serialize(msg);

        initializer.onMessage(buffer, Mockito.mock(RemoteConnection.class));
    }

    @Test
    public void testOnClose () {
        ClientHandler initializer = new ClientHandler(Mockito.mock(RegionManager.class), 1, Mockito.mock(Handler.class));
        initializer.onClose(Mockito.mock(RemoteConnection.class));
    }

    @Test
    public void testOnCloseLoggedInUser () {
        ClientHandler initializer = new ClientHandler(Mockito.mock(RegionManager.class), 1, Mockito.mock(Handler.class));
        initializer.regionContainer = Mockito.mock(RegionContainer.class);
        initializer.user = new User(1, "test", new ArrayList<>());
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
