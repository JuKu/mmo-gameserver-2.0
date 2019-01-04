package com.jukusoft.mmo.gs.region.subsystem.impl;

import com.jukusoft.mmo.gs.region.subsystem.SubSystemManager;
import com.jukusoft.mmo.gs.region.utils.TestSubSystem;
import io.vertx.core.Vertx;
import org.junit.Test;
import org.mockito.Mockito;

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

}
