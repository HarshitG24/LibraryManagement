package com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a request message for the Ricart-Agrawala distributed mutual exclusion algorithm.
 */
public class RicartAgrawalaRequest {
  private int requestCount;
  private String operation;
  private ConcurrentHashMap<String, AtomicInteger> vectorTimestamp;

  /**
   * Default constructor.
   */
  public RicartAgrawalaRequest() {

  }

  /**
   * Constructor with parameters.
   *
   * @param vectorTimestamp The vector timestamp of the request.
   * @param requestCount The number of requests sent so far.
   * @param operation The operation associated with the request.
   */
  public RicartAgrawalaRequest(ConcurrentHashMap<String, AtomicInteger> vectorTimestamp, int requestCount, String operation) {
    this.vectorTimestamp = vectorTimestamp;
    this.requestCount = requestCount;
    this.operation = operation;
  }

  /**
   * Retrieves the vector timestamp associated with the request.
   *
   * @return The vector timestamp.
   */
  public ConcurrentHashMap<String, AtomicInteger> getVectorTimestamp() {
    return vectorTimestamp;
  }

  /**
   * Sets the vector timestamp for the request.
   *
   * @param vectorTimestamp The vector timestamp to set.
   */
  public void setVectorTimestamp(ConcurrentHashMap<String, AtomicInteger> vectorTimestamp) {
    this.vectorTimestamp = vectorTimestamp;
  }

  /**
   * Retrieves the request count.
   *
   * @return The request count.
   */
  public int getRequestCount() {
    return requestCount;
  }

  /**
   * Sets the request count.
   *
   * @param requestCount The request count to set.
   */
  public void setRequestCount(int requestCount) {
    this.requestCount = requestCount;
  }

  /**
   * Retrieves the operation associated with the request.
   *
   * @return The operation.
   */
  public String getOperation() {
    return operation;
  }

  /**
   * Sets the operation for the request.
   *
   * @param operation The operation to set.
   */
  public void setOperation(String operation) {
    this.operation = operation;
  }
}
