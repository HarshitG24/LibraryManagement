package com.example.distributedsystems.distributed.systems.node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ContextClosedEventListener {

  private NodeManager nodeManager;

  @Autowired
  public void setNodeManager(NodeManager nodeManager) {
    this.nodeManager = nodeManager;
  }

  @EventListener(ContextClosedEvent.class)
  public void onApplicationEvent(ContextClosedEvent event) {
    nodeManager.stopNode();
  }
}
