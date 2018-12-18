package com.jukusoft.mmo.gs.region;

import com.jukusoft.mmo.engine.shared.config.Cache;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.utils.PlatformUtils;
import com.jukusoft.mmo.gs.region.ftp.NFtpFactory;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class RegionContainerImplTest {

    @Mock
    protected static Vertx vertx;

    @BeforeClass
    public static void beforeClass () throws IOException {
        if (new File("../config/ftp.cfg").exists()) {
            Config.load(new File("../config/ftp.cfg"));
        } else {
            Config.load(new File("../config/tests/travis.ftp.cfg"), false);
        }

        if (System.getProperties().contains("ftp.host")) {
            Config.set("FTP", "host", System.getProperty("ftp.host"));
        }

        if (System.getProperties().contains("ftp.port")) {
            Config.set("FTP", "port", System.getProperty("ftp.port"));
        }

        if (System.getProperties().contains("ftp.username")) {
            Config.set("FTP", "username", System.getProperty("ftp.username"));
        }

        if (System.getProperties().contains("ftp.password")) {
            Config.set("FTP", "password", System.getProperty("ftp.password"));
        }

        Cache.init("../cache/");

        Config.load(new File("../config/junit-logger.cfg"));
        Log.init();

        vertx = Mockito.mock(Vertx.class);

        //mock vertx.executeBlocking()
        Mockito.doAnswer(invocation -> {
            //execute handler
            Handler<Future<FTPClient>> handler = invocation.getArgument(0);
            Handler<AsyncResult<FTPClient>> resultHandler = invocation.getArgument(1);

            Future<FTPClient> future = Future.future();
            future.setHandler(event -> {
                resultHandler.handle(future);
            });
            handler.handle(future);

            return null;
        }).when(vertx).executeBlocking(any(Handler.class), any(Handler.class));

        NFtpFactory.init(vertx);
    }

    @AfterClass
    public static void afterClass () throws InterruptedException {
        vertx.close();

        Log.shutdown();

        Thread.sleep(200);
    }

    @Before
    public void setup() {
        //MockitoAnnotations.initMocks(this);
    }

    @After
    public void ensureFinish() {
        //
    }

    @Before
    public void windowsOnly() {
        //don't execute this tasks on travis ci
        org.junit.Assume.assumeTrue(PlatformUtils.isWindows());
    }

    @Test
    public void testConstructor () {
        new RegionContainerImpl(1, 1, 1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorNullRegion () {
        new RegionContainerImpl(0, 1, 1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorNullInstance () {
        new RegionContainerImpl(1, 0, 1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorNullShardID () {
        new RegionContainerImpl(1, 1, 0);
    }

    @Test
    public void testInit () throws InterruptedException {
        RegionContainer container = new RegionContainerImpl(1, 1, 1);
        assertEquals(false, ((RegionContainerImpl) container).initialized);
        assertEquals(false, ((RegionContainerImpl) container).ftpFilesLoaded);

        container.init();

        assertEquals(true, ((RegionContainerImpl) container).initialized);
        assertEquals(true, container.isInitialized());

        //because we mocked vertx.executeBlocking() to synchronous methods, we can check this here directly
        assertEquals(true, ((RegionContainerImpl) container).ftpFilesLoaded);

        //wait 200ms for logs
        Thread.sleep(200);
    }

    @Test
    public void testIndexClientCache () throws Exception {
        RegionContainerImpl container = new RegionContainerImpl("../junit-tests/ftp-files/static/regions/region_1_1/");
        container.indexClientCache();

        assertEquals(3, container.fileHashes.size());

        assertEquals(true, container.fileHashes.containsKey("test.txt"));
        assertEquals(true, container.fileHashes.containsKey("test2.txt"));
        assertEquals(true, container.fileHashes.containsKey("dir1/dir1test.txt"));

        //check, if all files are in list
        assertEquals("098f6bcd4621d373cade4e832627b4f6", container.fileHashes.get("test.txt"));
        assertEquals("ad0234829205b9033196ba818f7a872b", container.fileHashes.get("test2.txt"));
        assertEquals("ca2a0cf5b143e25b63fa7e2ef2ac54e7", container.fileHashes.get("dir1/dir1test.txt"));

        //wait 200ms for logs
        Thread.sleep(200);
    }

}
