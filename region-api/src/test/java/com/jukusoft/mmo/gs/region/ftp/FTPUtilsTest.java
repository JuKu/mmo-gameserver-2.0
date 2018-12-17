package com.jukusoft.mmo.gs.region.ftp;

import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.gs.region.utils.DummyHandler;
import io.github.bckfnn.ftp.FtpClient;
import io.github.bckfnn.ftp.FtpFile;
import io.vertx.core.Vertx;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.mockftpserver.fake.filesystem.WindowsFakeFileSystem;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FTPUtilsTest {

    //https://github.com/bckfnn/vertx-ftp-client/blob/3f33d076c10467908fd5f4591ebce809cae7cb48/src/main/java/io/github/bckfnn/ftp/FtpClient.java

    protected static Vertx vertx;
    protected static FakeFtpServer fakeFtpServer;

    @BeforeClass
    public static void beforeClass () throws IOException {
        String[] logger = new String[] {
                FtpClient.class.getName(),
                "io.netty.resolver.dns.DnsNameResolver",
                "io.netty.resolver.dns.DnsQueryContext",
                "com.zaxxer.hikari.pool.HikariPool",
                "io.netty.util.Recycler",
                "com.zaxxer.hikari.HikariConfig",
                "org.flywaydb.core.internal.util.scanner.classpath.ClassPathScanner",
                "org.flywaydb.core.internal.util.FeatureDetector",
                "org.flywaydb.core.internal.callback.SqlScriptFlywayCallback"
        };

        //set log levels to INFO to avoid sensitive information in logs
        for (String loggerName : logger) {
            ch.qos.logback.classic.Logger logger1 = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(loggerName);
            logger1.setLevel(ch.qos.logback.classic.Level.INFO);
        }

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

        //start ftp server
        /*fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount("test", "testpass", "/"));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/var/www/ftptest"));
        //fileSystem.add(new FileEntry("c:\\data\\file1.txt", "abcdef 1234567890"));
        //fileSystem.add(new FileEntry("c:\\data\\run.exe"));
        fakeFtpServer.setFileSystem(fileSystem);

        fakeFtpServer.start();
        int port = fakeFtpServer.getServerControlPort();
        System.err.println("start ftp server on port " + port);

        System.err.println("fake ftp server started.");

        try {
            Thread.sleep(300000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        vertx = Vertx.vertx();

        //check ftp connection
        FTPFactory.init(vertx);
    }

    @AfterClass
    public static void afterClass () {
        vertx.close();
        Config.clear();

        //fakeFtpServer.stop();
    }

    @Test
    public void testConstructor () {
        new FTPUtils();
    }

    @Test
    public void testCreateFTPConnection () {
        //create ftp connection
        FtpClient ftpClient = FTPFactory.createSync();
        assertNotNull(ftpClient);

        ftpClient.quit(new DummyHandler<>());
    }

    //@Test
    public void testListFiles () throws InterruptedException {
        //create ftp connection
        FtpClient ftpClient = FTPFactory.createSync();
        assertNotNull(ftpClient);

        String remoteDir = Config.get("FTP", "regionsDir") + "/region_1_1";
        System.err.println("testListFiles: " + remoteDir);

        AtomicInteger counter = new AtomicInteger(0);

        CountDownLatch latch = new CountDownLatch(1);
        FTPUtils.listFiles(ftpClient, remoteDir, latch, files -> {
            for (FTPFile file : files) {
                System.err.println("file: " + file.getName());
                counter.incrementAndGet();
            }
        });

        latch.await(3000, TimeUnit.MILLISECONDS);

        assertEquals(true, counter.get() > 0);

        ftpClient.quit(new DummyHandler<>());
    }

    @Test
    public void testFtpFile () {
        assertNotNull(FTPFile.from("drwxr-xr-x   2 user MMORPG       4096 Dec 15 00:38 server"));
    }

    @Test
    public void testLine () {
        String line = "drwxr-xr-x   2 ftp1@subdomain.example.com MMORPG       4096 Dec 15 00:38 client";

        Pattern filere = Pattern.compile("(.).........+(\\d+) ([A-Za-z0-9@\\.]+) +([A-Za-z0-9]+) +(\\d+) ([A-Z][a-z][a-z]) +(\\d+) +(\\d+):(\\d+) +([A-Za-z0-9@\\.]+)");
        Matcher m = filere.matcher(line);
        assertEquals(true, m.matches());

        assertEquals("d", m.group(1));
        assertEquals(2, Integer.parseInt(m.group(2)));
        assertEquals("ftp1@subdomain.example.com", m.group(3));
        assertEquals("MMORPG", m.group(4));
        assertEquals(4096, Integer.parseInt(m.group(5)));
        assertEquals("Dec", m.group(6));
        assertEquals(15, Integer.parseInt(m.group(7)));
        assertEquals(0, Integer.parseInt(m.group(8)));
        assertEquals(38, Integer.parseInt(m.group(9)));
        assertEquals("client", m.group(10));
    }

}
