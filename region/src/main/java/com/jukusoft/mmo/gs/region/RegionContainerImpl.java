package com.jukusoft.mmo.gs.region;

import com.jukusoft.mmo.engine.shared.config.Cache;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.memory.Pools;
import com.jukusoft.mmo.engine.shared.messages.DownloadRegionFilesRequest;
import com.jukusoft.mmo.engine.shared.messages.LoadMapResponse;
import com.jukusoft.mmo.engine.shared.messages.StartSyncGameStateRequest;
import com.jukusoft.mmo.engine.shared.messages.StartSyncGameStateResponse;
import com.jukusoft.mmo.engine.shared.utils.FileUtils;
import com.jukusoft.mmo.engine.shared.utils.HashUtils;
import com.jukusoft.mmo.gs.region.database.DBClient;
import com.jukusoft.mmo.gs.region.database.Database;
import com.jukusoft.mmo.gs.region.database.InvalideDatabaseException;
import com.jukusoft.mmo.gs.region.ftp.FTPUtil;
import com.jukusoft.mmo.gs.region.ftp.NFtpFactory;
import com.jukusoft.mmo.gs.region.handler.FileUpdaterHandler;
import com.jukusoft.mmo.gs.region.network.NetHandlerManager;
import com.jukusoft.mmo.gs.region.network.NetMessageHandler;
import com.jukusoft.mmo.gs.region.settings.Settings;
import com.jukusoft.mmo.gs.region.settings.impl.GlobalSettings;
import com.jukusoft.mmo.gs.region.subsystem.SubSystemManager;
import com.jukusoft.mmo.gs.region.subsystem.impl.CharacterDataService;
import com.jukusoft.mmo.gs.region.subsystem.impl.SubSystemManagerImpl;
import com.jukusoft.mmo.gs.region.subsystem.impl.WeatherSubSystem;
import com.jukusoft.mmo.gs.region.user.User;
import com.jukusoft.mmo.gs.region.utils.PlayerTuple;
import com.jukusoft.vertx.connection.clientserver.RemoteConnection;
import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.Serializer;
import com.jukusoft.vertx.serializer.utils.ByteUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RegionContainerImpl implements RegionContainer {

    protected static final String TUTORIAL_AREA = "tutorial";

    protected final String LOG_TAG;

    //region coordinates
    protected final long regionID;
    protected final int instanceID;
    protected final int shardID;

    //region attributes from static db
    protected String regionTitle = "";
    protected boolean locked = false;

    //cache path
    protected final String cachePath;

    protected boolean initialized = false;
    protected boolean ftpFilesLoaded = false;
    protected Lock ftpInitLock = new ReentrantLock(true);
    protected CountDownLatch ftpLatch = new CountDownLatch(1);

    protected Map<String,String> fileHashes = new HashMap<>();

    //queue with players which have connected while region was in initialization process
    protected Queue<PlayerTuple> waitingPlayerInitQueue = new ConcurrentLinkedQueue<>();

    protected final Vertx vertx;

    //message handler manager
    private final NetHandlerManager handlerManager = new NetHandlerManager();

    protected final SubSystemManager subSystemManager;

    //sql queries
    protected static final String SQL_GET_REGION = "SELECT * FROM `{prefix}regions` WHERE `regionID` = ? AND `instanceID` = ?; ";
    protected static final String SQL_GET_CHARACTER_POSITION = "SELECT * FROM `{prefix}character_positions` WHERE `cid` = ?; ";

    public RegionContainerImpl (Vertx vertx, long regionID, int instanceID, int shardID) {
        if (regionID <= 0) {
            throw new IllegalArgumentException("regionID has to be >= 1.");
        }

        if (instanceID <= 0) {
            throw new IllegalArgumentException("instanceID has to be >= 1.");
        }

        if (shardID <= 0) {
            throw new IllegalArgumentException("shardID has to be >= 1.");
        }

        this.vertx = vertx;

        this.regionID = regionID;
        this.instanceID = instanceID;
        this.shardID = shardID;

        this.cachePath = Cache.getInstance().getCachePath("regions/region_" + regionID + "_" + instanceID);

        LOG_TAG = "REG_" + regionID + "_" + instanceID + "_" + shardID;

        this.subSystemManager = new SubSystemManagerImpl(this.vertx, LOG_TAG, this.regionID, this.instanceID, this.shardID);
    }

    /**
    * constructor for junit tests
     *
     * @param vertx vertx instance
     * @param cachePath path to cache directory of region (ends with "/")
    */
    protected RegionContainerImpl (Vertx vertx, String cachePath) {
        this.vertx = vertx;
        this.regionID = 1;
        this.instanceID = 1;
        this.shardID = 1;

        this.LOG_TAG = "RegionCont";

        this.cachePath = cachePath;

        this.subSystemManager = null;
    }

    @Override
    public void init() {
        Log.i(LOG_TAG, "initialize region...");

        //download files for region from ftp server
        this.downloadFilesFromFtp();

        //load static region data from static database (they are fixed and cannot be changed at runtime - only with updates)
        this.loadStaticDataFromDB();

        //register message handlers
        this.handlers().register(DownloadRegionFilesRequest.class, new FileUpdaterHandler(LOG_TAG, this.regionID, this.instanceID, this.vertx, this.cachePath));

        this.handlers().register(StartSyncGameStateRequest.class, (msg, user, cid, conn) -> {
            Log.d(LOG_TAG, "start sync game state.");

            //load last character position from database (if it not exists --> character has joined the first time to this game)
            try (DBClient dbClient = Database.getClient()) {
                try (PreparedStatement statement = dbClient.prepareStatement(SQL_GET_CHARACTER_POSITION)) {
                    //set sql parameter
                    statement.setInt(1, cid);

                    try (ResultSet resultSet = statement.executeQuery()) {
                        float posX = 0;
                        float posY = 0;
                        float posZ = 0;

                        if (!resultSet.next()) {
                            //character position isn't inserted yet --> set character start position (from tutorial)
                            Settings settings = GlobalSettings.getInstance();
                            posX = settings.getFloat(TUTORIAL_AREA, "start_pos_x");
                            posY = settings.getFloat(TUTORIAL_AREA, "start_pos_y");
                            posZ = settings.getFloat(TUTORIAL_AREA, "start_pos_z");
                        } else {
                            //get position from db
                            posX = resultSet.getFloat("pos_x");
                            posY = resultSet.getFloat("pos_y");
                            posZ = resultSet.getFloat("pos_z");
                        }

                        //load character data
                        CharacterDataService characterService = this.subSystemManager.getSubSystem(CharacterDataService.class);
                        characterService.loadCharacter(user, cid);

                        //set player position and send them to client

                        StartSyncGameStateResponse response = Pools.get(StartSyncGameStateResponse.class);
                        response.posX = posX;
                        response.posY = posY;
                        response.posZ = posZ;

                        response.currentServerTime = System.currentTimeMillis();
                        response.staticObjects = new JsonObject();
                        response.currentGameWorldData = new JsonObject();
                        response.currentGameWorldData.put("env", new JsonObject());

                        //fill static objects data
                        this.subSystemManager.fillStaticObjects(response.staticObjects, user, cid, posX, posY, posZ);

                        //fill game world data
                        this.subSystemManager.fillGameWorldData(response.currentGameWorldData, user, cid, posX, posY, posZ);

                        Log.v(LOG_TAG, "send static objects data: " + response.staticObjects.encodePrettily());
                        Log.v(LOG_TAG, "send game world data: " + response.currentGameWorldData.encodePrettily());

                        conn.send(response);
                    }
                }
            } catch (Exception e) {
                Log.w(LOG_TAG, "Coulnd't load current player position. cid: " + cid, e);
            }
        });

        //TODO: load scripts and so on

        Log.i(LOG_TAG, "initialized region '" + this.regionTitle + "' successfully!");

        this.initialized = true;

        vertx.executeBlocking(event -> {
            try {
                ftpLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.initSubSystems();
        }, (Handler<AsyncResult<Void>>) event -> {
            //don't do anything here
        });
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    private void downloadFilesFromFtp () {
        ftpFilesLoaded = false;
        this.ftpLatch = new CountDownLatch(1);

        Log.d(LOG_TAG, "download region files from ftp server...");
        NFtpFactory.createAsync(ftpClient -> {
            //first, clear region cache so files, which was removed on ftp server are also removed here (else only existing files were overriden)
            Log.d(LOG_TAG, "clear region cache: " + this.cachePath);

            try {
                FileUtils.recursiveDeleteDirectory(new File(this.cachePath), false);
            } catch (IOException e) {
                Log.w(LOG_TAG, "Coulnd't delete region cache directory: " + this.cachePath, e);
            }

            String remoteDir = Config.get("FTP", "regionsDir") + "/region_" + +regionID + "_" + instanceID;
            Log.d(LOG_TAG, "try to download remote ftp directory: " + remoteDir);

            try {
                FTPUtil.downloadDirectory(ftpClient, remoteDir, this.cachePath);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Coulnd't download region files for region " + this.regionID + ", instanceID: " + this.instanceID + "!", e);
                return;
            }

            Log.d(LOG_TAG, "all ftp files received for region and stored in cache.");

            //index hashes
            try {
                this.indexClientCache();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Coulnd't index region ftp files: " + this.cachePath, e);
                return;
            }

            ftpInitLock.lock();
            ftpFilesLoaded = true;
            ftpLatch.countDown();
            ftpInitLock.unlock();

            //handle all waiting player init entries in queue
            PlayerTuple player = this.waitingPlayerInitQueue.poll();

            while (player != null) {
                this.initPlayer(player);

                //get next entry from queue
                player = this.waitingPlayerInitQueue.poll();
            }
        });
    }

    @Override
    public void initPlayer(User user, int cid, RemoteConnection conn) {
        Objects.requireNonNull(user, "user cannot be null.");
        Objects.requireNonNull(conn, "conn cannot be null.");

        if (this.locked) {
            //region is locked (maybe because a region update is occuring
            Log.w(LOG_TAG, "Cannot init player '" + user.getUsername() + "' (userID: " + user.getUserID() + ") with characterID " + cid + " because region is locked.");

            //TODO: add player to queue

            //TODO: send error message to client

            return;
        }

        Log.i(LOG_TAG, "init player '" + user.getUsername() + "' (userID: " + user.getUserID() + ") with characterID " + cid);
        PlayerTuple player = new PlayerTuple(user, cid, conn);

        ftpInitLock.lock();

        if (!ftpFilesLoaded) {
            //files aren't downloaded completely yet
            Log.i(LOG_TAG, "wait for downloading all ftp files...");

            this.waitingPlayerInitQueue.add(player);

            return;
        }

        ftpInitLock.unlock();

        //initialize player directly
        this.initPlayer(player);
    }

    @Override
    public void logoutPlayer(User user, int cid) {
        //TODO: add code here
    }

    private void initPlayer (PlayerTuple player) {
        Log.i(LOG_TAG, "initPlayer(): " + player);

        //send response to proxy / client
        LoadMapResponse response = Pools.get(LoadMapResponse.class);
        response.regionID = this.regionID;
        response.instanceID = this.instanceID;
        response.regionTitle = this.regionTitle;

        //add all required files with file checksums, so client can check, if map files are up to date
        for (Map.Entry<String,String> entry : this.fileHashes.entrySet()) {
            response.addRequiredMap(entry.getKey(), entry.getValue());
        }

        Log.d(LOG_TAG, "send LoadMapResponse to client.");

        //send message back to client
        player.conn.send(response);
    }

    @Override
    public void receive(Buffer buffer, User user, int cid, RemoteConnection conn) {
        if (!this.initialized || !this.ftpFilesLoaded) {
            throw new IllegalStateException("region isn't initialized yet.");
        }

        //unserialize message
        SerializableObject object = Serializer.unserialize(buffer);

        NetMessageHandler handler = handlers().findHandler(object.getClass());

        if (handler == null) {
            Log.w(LOG_TAG, "no handler is registered for message type " + ByteUtils.byteToHex(buffer.getByte(0)) + ", extendedType: " + ByteUtils.byteToHex(buffer.getByte(1)));
            throw new UnsupportedOperationException("no handler is registered for message type " + ByteUtils.byteToHex(buffer.getByte(0)) + ", extendedType: " + ByteUtils.byteToHex(buffer.getByte(1)));
        }

        //call handler
        handler.receive(object, user, cid, conn);
    }

    protected NetHandlerManager handlers() {
        return this.handlerManager;
    }

    protected void indexClientCache () throws Exception {
        this.fileHashes.clear();

        List<String> fileList = FileUtils.listFiles(new File(this.cachePath + "client/"));

        for (String filePath : fileList) {
            File f = new File(this.cachePath + "client/" + filePath);

            if (!f.exists()) {
                throw new IOException("Cannot index file, file doesn't exists: " + f.getAbsolutePath());
            }

            //calculate hash
            String fileHash = HashUtils.computeMD5FileHash(f);
            Log.v(LOG_TAG, "calculated hash for file '" + filePath + "': " + fileHash);

            this.fileHashes.put(filePath, fileHash);
        }
    }

    protected void loadStaticDataFromDB () {
        Log.i(LOG_TAG, "load static data from db...");

        try (DBClient dbClient = Database.getClient("static")) {
            try (PreparedStatement statement = dbClient.prepareStatement(SQL_GET_REGION)) {
                //set params
                statement.setLong(1, this.regionID);
                statement.setInt(2, this.instanceID);

                int counter = 0;

                // execute select SQL stetement
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        this.regionTitle = rs.getString("title");
                        this.locked = rs.getInt("locked") == 1;

                        //increment counter so we can verify, that min one result is available --> row exists
                        counter++;
                    }
                }

                if (counter <= 0) {
                    throw new InvalideDatabaseException("regionID " + this.regionID + ", instanceID: " + this.instanceID + " doesn't exists in database!");
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Cannot initialize region, because of SQLException while loading region static data from db: ", e);
            return;
        }

        Log.i(LOG_TAG, "loaded static data successfully!");
    }

    protected void initSubSystems () {
        Log.i(LOG_TAG, "initialize subsystems...");

        //add subsystems
        this.subSystemManager.addSubSystem(WeatherSubSystem.class, new WeatherSubSystem());
        this.subSystemManager.addSubSystem(CharacterDataService.class, new CharacterDataService());
    }

}
