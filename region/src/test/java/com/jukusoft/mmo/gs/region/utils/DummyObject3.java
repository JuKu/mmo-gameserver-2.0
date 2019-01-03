package com.jukusoft.mmo.gs.region.utils;

import com.jukusoft.mmo.gs.region.subsystem.InjectSubSystem;
import com.jukusoft.mmo.gs.region.subsystem.impl.WeatherSubSystem;

public class DummyObject3 {

    @InjectSubSystem (nullable = false)
    protected WeatherSubSystem weatherSubSystem;

}
