package com.jukusoft.mmo.gs.region;

import com.jukusoft.mmo.engine.shared.config.Cache;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.utils.PlatformUtils;
import com.jukusoft.mmo.gs.region.ftp.NFtpFactory;
import io.vertx.core.Vertx;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

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
    public void testInit () {
        RegionContainer container = new RegionContainerImpl(1, 1, 1);
        assertEquals(false, ((RegionContainerImpl) container).initialized);

        container.init();

        assertEquals(true, ((RegionContainerImpl) container).initialized);
    }

}
