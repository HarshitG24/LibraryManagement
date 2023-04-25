package com.example.distributedsystems.distributed.systems.node;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class NodeRegistry {

  @Autowired
  private HazelcastInstance hazelcastInstance;

  public UUID registerNode(String nodeAddress) {
    hazelcastInstance.getMap("activeNodes").put(nodeAddress, true);
    return hazelcastInstance.getLifecycleService().addLifecycleListener(event -> {
      if (event.getState() == LifecycleEvent.LifecycleState.SHUTTING_DOWN) {
        hazelcastInstance.getMap("activeNodes").remove(nodeAddress);
      }
    });
  }

  public Set<String> getActiveNodes() {
    Set<Object> keys = hazelcastInstance.getMap("activeNodes").keySet();
    return keys.stream().map(Object::toString).collect(Collectors.toSet());
  }

}
