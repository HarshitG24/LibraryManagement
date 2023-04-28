package com.example.distributedsystems.distributed.systems.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up Hazelcast instance and configuration
 */
@Configuration
public class HazelcastConfig {

  /**
   * Defines the Hazelcast configuration properties such as instance name, maps and logging.
   * Also defines the join configuration using TCP/IP.
   *
   * @return Hazelcast Config object
   */
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

  /**
   * Creates a Hazelcast instance using the provided Hazelcast configuration
   *
   * @param hazelcastConfiguration the Hazelcast configuration object
   * @return HazelcastInstance object
   */
  @Bean
  public HazelcastInstance hazelcastInstance(Config hazelcastConfiguration) {
    return Hazelcast.newHazelcastInstance(hazelcastConfiguration);
  }
}
