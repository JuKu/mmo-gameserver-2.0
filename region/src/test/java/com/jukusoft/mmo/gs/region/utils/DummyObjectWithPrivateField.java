package com.jukusoft.mmo.gs.region.utils;

import com.jukusoft.mmo.gs.region.subsystem.InjectSubSystem;

public class DummyObjectWithPrivateField {

    @InjectSubSystem
    private TestSubSystem testSubSystem;

    public TestSubSystem getTestSubSystem() {
        return testSubSystem;
    }

}
