package com.jukusoft.mmo.gs.frontend.log;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.jukusoft.mmo.engine.shared.config.Config;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;

public class HzLoggerTest {

    @Test (expected = NullPointerException.class)
    public void testNullConstructor () {
        new HzLogger(null);
    }

    @Test
    public void testConstructor () {
        Config.set("Hazelcast", "logTopicName", "test");
        HazelcastInstance hazelcastInstance = Mockito.mock(HazelcastInstance.class);
    }

    @Test
    public void testLog () {
        Config.set("Hazelcast", "logTopicName", "test");
        HazelcastInstance hazelcastInstance = Mockito.mock(HazelcastInstance.class);
        Mockito.when(hazelcastInstance.getTopic(any(String.class))).thenReturn(Mockito.mock(ITopic.class));

        HzLogger logger = new HzLogger(hazelcastInstance);
        logger.log("test message");
    }

}
