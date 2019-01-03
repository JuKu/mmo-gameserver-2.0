package com.jukusoft.mmo.gs.region.utils;

import com.jukusoft.mmo.gs.region.subsystem.InjectSubSystem;
import com.jukusoft.mmo.gs.region.subsystem.SubSystemAdapter;
import com.jukusoft.mmo.gs.region.subsystem.impl.WeatherSubSystem;

@InjectSubSystem
public class DummyObjectWithAnnotation {

    protected TestSubSystem testSubSystem;

    protected WeatherSubSystem weatherSubSystem;

}
