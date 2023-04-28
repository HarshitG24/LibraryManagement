package com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a reply message for the Ricart-Agrawala distributed mutual exclusion algorithm.
 */
public class RicartAgrawalaReply {
  private boolean granted;
  private ConcurrentHashMap<String, AtomicInteger> vectorTimestamp;

  /**
   * Constructs a new RicartAgrawalaReply with the specified granted status and vector timestamp.
   *
   * @param granted The granted status of the request.
   * @param vectorTimestamp The vector timestamp associated with the reply.
   */
  public RicartAgrawalaReply(boolean granted, ConcurrentHashMap<String, AtomicInteger> vectorTimestamp) {
    this.granted = granted;
    this.vectorTimestamp = vectorTimestamp;
  }

  /**
   * Returns the granted status of the requested lock.
   *
   * @return True if the requested lock is granted, false otherwise.
   */
  public boolean isGranted() {
    return granted;
  }

  /**
   * Sets the granted status of the requested lock.
   *
   * @param granted True if the requested lock is granted, false otherwise.
   */
  public void setGranted(boolean granted) {
    this.granted = granted;
  }

  /**
   * Returns the vector timestamp associated with the reply.
   *
   * @return The vector timestamp.
   */
  public ConcurrentHashMap<String, AtomicInteger> getVectorTimestamp() {
    return vectorTimestamp;
  }

  /**
   * Sets the vector timestamp associated with the reply.
   *
   * @param vectorTimestamp The vector timestamp.
   */
  public void setVectorTimestamp(ConcurrentHashMap<String, AtomicInteger> vectorTimestamp) {
    this.vectorTimestamp = vectorTimestamp;
  }
}
