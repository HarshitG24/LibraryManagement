package com.example.distributedsystems.distributed.systems.node;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class NodeRegistry {
  private static final Logger logger = LoggerFactory.getLogger(NodeRegistry.class);

  private final RestTemplate restTemplate = new RestTemplate();

  @Autowired
  private HazelcastInstance hazelcastInstance;

  public UUID registerNode(String nodeAddress) {
    hazelcastInstance.getMap("activeNodes").put(nodeAddress, true);
    return hazelcastInstance.getLifecycleService().addLifecycleListener(event -> {
      if (event.getState() == LifecycleEvent.LifecycleState.SHUTTING_DOWN) {
        logger.info("Unregistering node from cluster");
        hazelcastInstance.getMap("activeNodes").remove(nodeAddress);
        updateVectorTimestampsForAllNodesExceptCurrent();
        logger.info("Current active nodes after unregistering self: " + getActiveNodes());
      }
    });
  }

  public Set<String> getActiveNodes() {
    Set<Object> keys = hazelcastInstance.getMap("activeNodes").keySet();
    return keys.stream().map(Object::toString).collect(Collectors.toSet());
  }

  public void removeNodeFromActiveNodes(String nodeAddress) {
    hazelcastInstance.getMap("activeNodes").remove(nodeAddress);
  }

  private void updateVectorTimestampsForAllNodesExceptCurrent() {
    for (String nodeAddress : getActiveNodes()) {
        restTemplate.postForEntity(nodeAddress + "/vectorTimestamps/update", null, String.class);
    }
  }
}
