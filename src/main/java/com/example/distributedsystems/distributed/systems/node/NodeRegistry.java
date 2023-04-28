package com.example.distributedsystems.distributed.systems.node;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
/**
 * This class represents the registry of nodes in the cluster.
 * It allows registration of new nodes and removal of nodes that are shutting down.
 * It also provides methods to retrieve the set of active nodes in the cluster.
 */
@Component
public class NodeRegistry {
  private static final Logger logger = LoggerFactory.getLogger(NodeRegistry.class);

  private final RestTemplate restTemplate = new RestTemplate();

  @Autowired
  private HazelcastInstance hazelcastInstance;

  /**
   * Registers a new node in the cluster.
   *
   * @param nodeAddress the address of the new node
   * @return a UUID representing the registration of the new node
   */
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

  /**
   * Retrieves the set of active nodes in the cluster.
   *
   * @return a set of active nodes in the cluster
   */
  public Set<String> getActiveNodes() {
    Set<Object> keys = hazelcastInstance.getMap("activeNodes").keySet();
    return keys.stream().map(Object::toString).collect(Collectors.toSet());
  }

  /**
   * Removes a node from the set of active nodes in the cluster.
   * @param nodeAddress the address of the node to be removed
   */
    public void removeNodeFromActiveNodes(String nodeAddress) {
    hazelcastInstance.getMap("activeNodes").remove(nodeAddress);
  }

  /**
   * Updates the vector timestamps for all nodes in the cluster except for the current node.
   */
  private void updateVectorTimestampsForAllNodesExceptCurrent() {
    for (String nodeAddress : getActiveNodes()) {
        restTemplate.postForEntity(nodeAddress + "/vectorTimestamps/update", null, String.class);
    }
  }
}
