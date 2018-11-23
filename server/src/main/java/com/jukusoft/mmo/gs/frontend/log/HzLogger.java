package com.jukusoft.mmo.gs.frontend.log;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.LogListener;

import java.util.Objects;

public class HzLogger implements LogListener {

    protected final ITopic<String> topic;

    /**
    * default constructor
     *
     * @param hazelcastInstance hazelcast instance
    */
    public HzLogger(HazelcastInstance hazelcastInstance) {
        Objects.requireNonNull(hazelcastInstance);
        topic = hazelcastInstance.getTopic(Config.get("Hazelcast", "logTopicName"));
    }

    @Override
    public void log(String str) {
        topic.publish(str);
    }

}
