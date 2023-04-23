package com.example.distributedsystems.distributed.systems.node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationReadyEventListener {

  private NodeManager nodeManager;

  @Autowired
  public void setNodeManager(NodeManager nodeManager) {
    this.nodeManager = nodeManager;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationEvent(ApplicationReadyEvent event) {
    nodeManager.startNode();
  }
}
