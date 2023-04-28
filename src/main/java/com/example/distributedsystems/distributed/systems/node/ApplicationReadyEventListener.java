package com.example.distributedsystems.distributed.systems.node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * A listener that triggers the node to start when the application is ready.
 */
@Component
public class ApplicationReadyEventListener {

  private NodeManager nodeManager;

  /**
   * Sets the NodeManager instance to be used by this listener.
   *
   * @param nodeManager The NodeManager instance to be used.
   */
  @Autowired
  public void setNodeManager(NodeManager nodeManager) {
    this.nodeManager = nodeManager;
  }

  /**
   * Event listener that starts the node when the application is ready.
   *
   * @param event The ApplicationReadyEvent instance containing the event information.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationEvent(ApplicationReadyEvent event) {
    nodeManager.startNode();
  }
}
