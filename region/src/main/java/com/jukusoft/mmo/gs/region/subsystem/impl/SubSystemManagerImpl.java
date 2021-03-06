package com.jukusoft.mmo.gs.region.subsystem.impl;

import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.gs.region.subsystem.GameWorldDataHolder;
import com.jukusoft.mmo.gs.region.subsystem.StaticObjectsHolder;
import com.jukusoft.mmo.gs.region.subsystem.SubSystem;
import com.jukusoft.mmo.gs.region.subsystem.SubSystemManager;
import com.jukusoft.mmo.gs.region.user.User;
import com.jukusoft.mmo.gs.region.utils.DIUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.mini2Dx.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SubSystemManagerImpl implements SubSystemManager {

    protected static final int INITIAL_CAPACITY = 20;

    protected final String LOG_TAG;

    //array with all subsystems
    protected Array<SubSystem> list = new Array(false, INITIAL_CAPACITY);

    //map with subsystems
    protected Map<Class,SubSystem> classToSubSystemMap = new HashMap<>(INITIAL_CAPACITY);

    protected final Vertx vertx;
    protected final long regionID;
    protected final int instanceID;
    protected final int shardID;

    public SubSystemManagerImpl (Vertx vertx, String LOG_TAG, long regionID, int instanceID, int shardID) {
        this.vertx = vertx;
        this.regionID = regionID;
        this.instanceID = instanceID;
        this.shardID = shardID;

        this.LOG_TAG = LOG_TAG;
    }

    @Override
    public <T extends SubSystem> void addSubSystem(Class<T> cls, T system) {
        Objects.requireNonNull(cls);
        Objects.requireNonNull(system);

        //inject other subsystems
        DIUtils.injectSubSystems(system, system.getClass(), this.classToSubSystemMap);

        //first, initialize subsystem
        system.init(this.vertx, this.regionID, this.instanceID, this.shardID);

        Log.v(LOG_TAG, "added subsystem: " + system.getClass().getSimpleName());
        this.classToSubSystemMap.put(system.getClass(), system);
        this.list.add(system);
    }

    @Override
    public <T extends SubSystem> void removeSubSystem(Class<T> cls) {
        SubSystem system = this.classToSubSystemMap.get(cls);

        if (system == null) {
            //subsystem doesn't exists
            throw new IllegalStateException("subsystem '" + cls.getCanonicalName() + "' wasn't added before!");
        }

        this.list.removeValue(system, false);
        system.shutdown();
    }

    @Override
    public <T extends SubSystem> T getSubSystem(Class<T> cls) {
        Object subSystem = this.classToSubSystemMap.get(cls);

        if (subSystem == null) {
            throw new IllegalStateException("subsystem with class '" + cls.getCanonicalName() + "' doesn't exists!");
        }

        return cls.cast(subSystem);
    }

    @Override
    public void fillStaticObjects(JsonObject json, User user, int cid, float posX, float posY, float posZ) {
        for (SubSystem system : list) {
            if (system instanceof StaticObjectsHolder) {
                ((StaticObjectsHolder) system).fillStaticObjects(json, user, cid, posX, posY, posZ);
            }
        }
    }

    @Override
    public void fillGameWorldData(JsonObject json, User user, int cid, float posX, float posY, float posZ) {
        for (SubSystem system : list) {
            if (system instanceof GameWorldDataHolder) {
                ((GameWorldDataHolder) system).fillGameWorldData(json, user, cid, posX, posY, posZ);
            }
        }
    }

}
