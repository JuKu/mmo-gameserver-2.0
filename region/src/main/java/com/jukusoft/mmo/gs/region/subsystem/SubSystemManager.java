package com.jukusoft.mmo.gs.region.subsystem;

public interface SubSystemManager {

    /**
    * add subsystem
     *
     * @param system instance of subsystem
    */
    public void addSubSystem (String name, SubSystem system);

    /**
    * remove subsystem
     *
     * @param system instance of subsystem
    */
    public void removeSubSystem (SubSystem system);

    /**
     * remove subsystem
     *
     * @param name name of subsystem
     */
    public void removeSubSystem (String name);

}
