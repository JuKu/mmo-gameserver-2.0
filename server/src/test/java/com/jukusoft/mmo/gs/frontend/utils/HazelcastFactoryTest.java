package com.jukusoft.mmo.gs.frontend.utils;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.jukusoft.mmo.engine.shared.config.Config;
import org.junit.Test;

public class HazelcastFactoryTest {

    @Test
    public void testConstructor () {
        new HazelcastFactory();
    }

    @Test (timeout = 30000)
    public void testCreateHzInstanceFromConfig () {
        com.hazelcast.config.Config config = new com.hazelcast.config.Config();
        config.getNetworkConfig().setPort(5710)
                .setPortAutoIncrement(false);
        config.getGroupConfig().setName("test").setPassword("test");
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        System.err.println("started hazelcast server.");

        //set example values
        Config.set("Hazelcast", "ip", "127.0.0.1");
        Config.set("Hazelcast", "port", "5710");
        Config.set("Hazelcast", "user", "test");
        Config.set("Hazelcast", "password", "test");
        Config.set("Hazelcast", "token", "test-token");

        HazelcastInstance hz = HazelcastFactory.createHzInstanceFromConfig();
        System.err.println("started hazelcast client.");

        //shutdown hazelcast instances
        hazelcastInstance.shutdown();
        hz.shutdown();
    }

}
