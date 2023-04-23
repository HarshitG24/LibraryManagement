package com.example.distributedsystems.distributed.systems.node;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class HazelcastConfig {

  @Bean
  public Config hazelcastConfiguration() {
    Config config = new Config();
    config.setInstanceName("hazelcast-instance")
            .addMapConfig(new MapConfig().setName("activeNodes"))
            .setProperty("hazelcast.logging.type", "slf4j");

    JoinConfig joinConfig = config.getNetworkConfig().getJoin();
    joinConfig.getTcpIpConfig().setEnabled(true)
            .addMember("127.0.0.1");

    joinConfig.getMulticastConfig().setEnabled(false);
    return config;
  }

  @Bean
  public HazelcastInstance hazelcastInstance(Config hazelcastConfiguration) {
    return Hazelcast.newHazelcastInstance(hazelcastConfiguration);
  }
}
