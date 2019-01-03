package com.jukusoft.mmo.gs.region.utils;

import com.jukusoft.mmo.gs.region.subsystem.RequiredSubSystemNotFoundException;
import com.jukusoft.mmo.gs.region.subsystem.SubSystem;
import com.jukusoft.mmo.gs.region.subsystem.impl.WeatherSubSystem;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DIUtilsTest {

    @Test
    public void testConstructor () {
        new DIUtils();
    }

    @Test
    public void testInjectSubSystemsInObjectWithoutFields () {
        Map<Class, SubSystem> map = new HashMap<>();
        DIUtils.injectSubSystems(new DummyObject(), DummyObject.class, map);
    }

    @Test
    public void testInjectSubSystemsWithMissingNullableSubSystem () {
        Map<Class, SubSystem> map = new HashMap<>();
        DIUtils.injectSubSystems(new DummyObject2(), DummyObject2.class, map);
    }

    @Test (expected = RequiredSubSystemNotFoundException.class)
    public void testInjectSubSystemsWithMissingSubSystem () {
        Map<Class, SubSystem> map = new HashMap<>();
        DIUtils.injectSubSystems(new DummyObject3(), DummyObject3.class, map);
    }

    @Test
    public void testInjectExistingSubSystem () {
        Map<Class, SubSystem> map = new HashMap<>();
        map.put(TestSubSystem.class, new TestSubSystem());

        DummyObject4 testObj = new DummyObject4();
        assertNull(testObj.testSubSystem);

        DIUtils.injectSubSystems(testObj, DummyObject4.class, map);
        assertNotNull(testObj.testSubSystem);
    }

    @Test
    public void testInjectClassWithAnnoation () {
        Map<Class, SubSystem> map = new HashMap<>();
        map.put(TestSubSystem.class, new TestSubSystem());
        map.put(WeatherSubSystem.class, new WeatherSubSystem());

        DummyObjectWithAnnotation testObj = new DummyObjectWithAnnotation();
        assertNull(testObj.testSubSystem);
        assertNull(testObj.weatherSubSystem);

        DIUtils.injectSubSystems(testObj, DummyObjectWithAnnotation.class, map);
        assertNotNull(testObj.testSubSystem);
        assertNotNull(testObj.weatherSubSystem);
    }

    @Test
    public void testInjectExistingSubSystemWithPrivateField () {
        Map<Class, SubSystem> map = new HashMap<>();
        map.put(TestSubSystem.class, new TestSubSystem());

        DummyObjectWithPrivateField testObj = new DummyObjectWithPrivateField();
        assertNull(testObj.getTestSubSystem());

        DIUtils.injectSubSystems(testObj, DummyObject4.class, map);
        assertNotNull(testObj.getTestSubSystem());
    }

    @Test
    public void testInjectFinalField () {
        Map<Class, SubSystem> map = new HashMap<>();
        map.put(TestSubSystem.class, new TestSubSystem());

        DummyObjectWithFinalField testObj = new DummyObjectWithFinalField();
        assertNull(testObj.testSubSystem);

        DIUtils.injectSubSystems(testObj, DummyObjectWithFinalField.class, map);
        assertNotNull(testObj.testSubSystem);
    }

    @Test (expected = RuntimeException.class)
    public void testInjectStaticFinalField () {
        Map<Class, SubSystem> map = new HashMap<>();
        map.put(TestSubSystem.class, new TestSubSystem());

        DummyObjectWithStaticFinalField testObj = new DummyObjectWithStaticFinalField();
        DIUtils.injectSubSystems(testObj, DummyObjectWithFinalField.class, map);
    }

}
