package com.jukusoft.mmo.gs.region.subsystem.impl;

import com.jukusoft.mmo.gs.region.subsystem.SubSystemManager;
import com.jukusoft.mmo.gs.region.user.User;
import com.jukusoft.mmo.gs.region.utils.TestSubSystem;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class SubSystemManagerImplTest {

    @Test
    public void testConstructor () {
        new SubSystemManagerImpl(Mockito.mock(Vertx.class), "region_1_1", 1, 1, 1);
    }

    @Test
    public void testAddSubSystem () {
        SubSystemManager subSystemManager = new SubSystemManagerImpl(Mockito.mock(Vertx.class), "region_1_1", 1, 1, 1);

        //add subsystem
        TestSubSystem testSubSystem = new TestSubSystem();
        subSystemManager.addSubSystem(TestSubSystem.class, testSubSystem);
    }

    @Test (expected = IllegalStateException.class)
    public void testRemoveNotExistentSubSystem () {
        SubSystemManager subSystemManager = new SubSystemManagerImpl(Mockito.mock(Vertx.class), "region_1_1", 1, 1, 1);
        subSystemManager.removeSubSystem(TestSubSystem.class);
    }

    @Test
    public void testRemoveSubSystem () {
        SubSystemManager subSystemManager = new SubSystemManagerImpl(Mockito.mock(Vertx.class), "region_1_1", 1, 1, 1);

        //add subsystem
        TestSubSystem testSubSystem = new TestSubSystem();
        subSystemManager.addSubSystem(TestSubSystem.class, testSubSystem);

        subSystemManager.removeSubSystem(TestSubSystem.class);
    }

    @Test (expected = IllegalStateException.class)
    public void testGetNotExistentSubSystem () {
        SubSystemManager subSystemManager = new SubSystemManagerImpl(Mockito.mock(Vertx.class), "region_1_1", 1, 1, 1);
        subSystemManager.getSubSystem(TestSubSystem.class);
    }

    @Test
    public void testGetSubSystem () {
        SubSystemManager subSystemManager = new SubSystemManagerImpl(Mockito.mock(Vertx.class), "region_1_1", 1, 1, 1);

        //add subsystem
        TestSubSystem testSubSystem = new TestSubSystem();
        subSystemManager.addSubSystem(TestSubSystem.class, testSubSystem);

        subSystemManager.getSubSystem(TestSubSystem.class);
        assertNotNull(subSystemManager.getSubSystem(TestSubSystem.class));
    }

    @Test
    public void testFillStaticObjects () {
        SubSystemManager subSystemManager = new SubSystemManagerImpl(Mockito.mock(Vertx.class), "region_1_1", 1, 1, 1);

        //add subsystems
        subSystemManager.addSubSystem(TestStaticObjectsHolderSubSystem.class, new TestStaticObjectsHolderSubSystem());
        subSystemManager.addSubSystem(TestSubSystem.class, new TestSubSystem());

        JsonObject json = new JsonObject();
        subSystemManager.fillStaticObjects(json, new User(1, "username", new ArrayList<>()), 2, 1, 1, 1);

        //check, if method fillStaticObjects of subsystem was executed
        assertEquals(true, json.containsKey("test"));
        assertEquals("test2", json.getString("test"));
    }

    @Test
    public void testFillGameWorldData () {

        //add subsystems
        SubSystemManager subSystemManager = new SubSystemManagerImpl(Mockito.mock(Vertx.class), "region_1_1", 1, 1, 1);
        subSystemManager.addSubSystem(TestGameWorldDataHolderSubSystem.class, new TestGameWorldDataHolderSubSystem());
        subSystemManager.addSubSystem(TestSubSystem.class, new TestSubSystem());

        JsonObject json = new JsonObject();
        subSystemManager.fillGameWorldData(json, new User(1, "username", new ArrayList<>()), 2, 1, 1, 1);

        //check, if method fillStaticObjects of subsystem was executed
        assertEquals(true, json.containsKey("test"));
        assertEquals("test2", json.getString("test"));
    }

}
