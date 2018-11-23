package com.jukusoft.mmo.gs.frontend.utils;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.jukusoft.mmo.engine.shared.config.Config;

public class HazelcastFactory {

    protected static final String HAZELCAST_TAG = "Hazelcast";

    protected HazelcastFactory() {
        //private
    }

    public static HazelcastInstance getHazelcastInstance(String ip, int port, String user, String password) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getGroupConfig().setName(user).setPassword(password);
        clientConfig.getNetworkConfig().addAddress(ip + ":" + port);
        clientConfig.setProperty("hazelcast.application.validation.token", Config.get(HAZELCAST_TAG, "token"));
        return HazelcastClient.newHazelcastClient(clientConfig);
    }

    public static HazelcastInstance createHzInstanceFromConfig () {
        String ip = Config.get(HAZELCAST_TAG, "ip");
        int port = Config.getInt(HAZELCAST_TAG, "port");
        String user = Config.get(HAZELCAST_TAG, "user");
        String password = Config.get(HAZELCAST_TAG, "password");

        return getHazelcastInstance(ip, port, user, password);
    }

}
