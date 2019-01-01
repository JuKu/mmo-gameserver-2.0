package com.jukusoft.mmo.gs.region.subsystem.impl;

import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
import com.jukusoft.mmo.gs.region.subsystem.SubSystem;
import com.jukusoft.mmo.gs.region.subsystem.SubSystemManager;
import io.vertx.core.Vertx;
import org.mini2Dx.gdx.utils.Array;

public class SubSystemManagerImpl implements SubSystemManager {

    protected static final int INITIAL_CAPACITY = 20;

    protected final String LOG_TAG;

    //array with all subsystems
    protected Array<SubSystem> list = new Array(false, INITIAL_CAPACITY);

    //map with subsystems
    protected ObjectObjectMap<String,SubSystem> subSystemsMap = new ObjectObjectHashMap<>(INITIAL_CAPACITY);

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
    public void addSubSystem(String name, SubSystem system) {
        //first, initialize subsystem
        system.init(this.vertx, this.regionID, this.instanceID, this.shardID);

        this.subSystemsMap.put(name, system);
        this.list.add(system);
    }

    @Override
    public void removeSubSystem(SubSystem system) {
        this.list.removeValue(system, false);
        system.shutdown();

        //remove all name - system entries with the same object
        this.subSystemsMap.removeAll((key, value) -> {
            if (value.equals(system)) {
                return true;
            } else {
                return false;
            }
        });
    }

    @Override
    public void removeSubSystem(String name) {
        SubSystem system = this.subSystemsMap.get(name);

        if (system == null) {
            //subsystem doesn't exists
            return;
        }

        this.list.removeValue(system, false);
        this.subSystemsMap.remove(name);
        system.shutdown();
    }

}
