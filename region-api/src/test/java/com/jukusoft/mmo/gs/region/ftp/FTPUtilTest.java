package com.jukusoft.mmo.gs.region.ftp;

import com.jukusoft.mmo.engine.shared.config.Cache;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.utils.FileUtils;
import com.jukusoft.mmo.engine.shared.utils.PlatformUtils;
import io.github.bckfnn.ftp.FtpClient;
import io.vertx.core.Vertx;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class FTPUtilTest {

    protected static final String CONFIG_SECTION = "FTP";

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

        //create cache directory
        File cacheDir = new File(Cache.getInstance().getCachePath("junit-ftp-tests"));

        if (cacheDir.exists()) {
            cacheDir.delete();
        }

        System.err.println("create cache dir: " + cacheDir.getAbsolutePath());
        cacheDir.mkdirs();
    }

    @AfterClass
    public static void afterClass () {
        Config.clear();
    }

    @Before
    public void windowsOnly() {
        //don't execute this tasks on travis ci
        org.junit.Assume.assumeTrue(PlatformUtils.isWindows());
    }

    @Before
    public void windowsOnlyAfter() {
        //
    }

    @Test
    public void testConstructor () {
        new FTPUtil();
    }

    @Test
    public void testConnect () throws IOException {
        String server = Config.get(CONFIG_SECTION, "host");
        int port = Config.getInt(CONFIG_SECTION, "port");
        String user = Config.get(CONFIG_SECTION, "username");
        String pass = Config.get(CONFIG_SECTION, "password");

        FTPClient ftpClient = new FTPClient();

        // connect and login to the server
        ftpClient.connect(server, port);
        ftpClient.login(user, pass);

        // log out and disconnect from the server
        ftpClient.logout();
        ftpClient.disconnect();
    }

    @Test
    public void testDownloadDir () throws IOException {
        assertEquals(false, new File(Cache.getInstance().getCachePath("junit-ftp-tests") + "my-test.txt").exists());

        String server = Config.get(CONFIG_SECTION, "host");
        int port = Config.getInt(CONFIG_SECTION, "port");
        String user = Config.get(CONFIG_SECTION, "username");
        String pass = Config.get(CONFIG_SECTION, "password");

        FTPClient ftpClient = new FTPClient();

        // connect and login to the server
        ftpClient.connect(server, port);
        ftpClient.login(user, pass);

        // use local passive mode to pass firewall
        ftpClient.enterLocalPassiveMode();

        System.out.println("Connected!");

        String remoteDirPath = Config.get("FTP", "regionsDir") + "/region_1_1";
        System.out.println("remoteDirPath: " + remoteDirPath);

        String saveDirPath = Cache.getInstance().getCachePath("junit-ftp-tests");

        FTPUtil.downloadDirectory(ftpClient, remoteDirPath, saveDirPath);

        // log out and disconnect from the server
        ftpClient.logout();
        ftpClient.disconnect();

        assertEquals(true, new File(Cache.getInstance().getCachePath("junit-ftp-tests") + "my-test.txt").exists());
        assertEquals("test1234", FileUtils.readFile(Cache.getInstance().getCachePath("junit-ftp-tests") + "my-test.txt", StandardCharsets.UTF_8));
    }

}
