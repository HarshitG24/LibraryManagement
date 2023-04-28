package com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a release message for the Ricart-Agrawala distributed mutual exclusion algorithm.
 */
public class RicartAgrawalaRelease {
  private String operation;
  private ConcurrentHashMap<String, AtomicInteger> vectorTimestamp;

  /**
   * Constructs an empty RicartAgrawalaRelease.
   */
  public RicartAgrawalaRelease() {
  }

  /**
   * Constructs a new RicartAgrawalaRelease with the specified operation and vector timestamp.
   *
   * @param operation The operation being released.
   * @param vectorTimestamp The vector timestamp associated with the release.
   */
  public RicartAgrawalaRelease(String operation, ConcurrentHashMap<String, AtomicInteger> vectorTimestamp) {
    this.operation = operation;
    this.vectorTimestamp = vectorTimestamp;
  }

  /**
   * Returns the operation associated with the release.
   *
   * @return The operation being released.
   */
  public String getOperation() {
    return operation;
  }

  /**
   * Sets the operation associated with the release.
   *
   * @param operation The operation being released.
   */
  public void setOperation(String operation) {
    this.operation = operation;
  }

  /**
   * Returns the vector timestamp associated with the release.
   *
   * @return The vector timestamp.
   */
  public ConcurrentHashMap<String, AtomicInteger> getVectorTimestamp() {
    return vectorTimestamp;
  }

  /**
   * Sets the vector timestamp associated with the release.
   *
   * @param vectorTimestamp The vector timestamp.
   */
  public void setVectorTimestamp(ConcurrentHashMap<String, AtomicInteger> vectorTimestamp) {
    this.vectorTimestamp = vectorTimestamp;
  }
}
