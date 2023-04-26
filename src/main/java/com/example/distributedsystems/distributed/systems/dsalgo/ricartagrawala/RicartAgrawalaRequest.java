package com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RicartAgrawalaRequest {
//  private long timestamp;
  private int requestCount;
  private String operation;
  private ConcurrentHashMap<String, AtomicInteger> vectorTimestamp;


  public RicartAgrawalaRequest() {

  }

  public RicartAgrawalaRequest(ConcurrentHashMap<String, AtomicInteger> vectorTimestamp, int requestCount, String operation) {
    this.vectorTimestamp = vectorTimestamp;
    this.requestCount = requestCount;
    this.operation = operation;
  }

  public ConcurrentHashMap<String, AtomicInteger> getVectorTimestamp() {
    return vectorTimestamp;
  }

  public void setVectorTimestamp(ConcurrentHashMap<String, AtomicInteger> vectorTimestamp) {
    this.vectorTimestamp = vectorTimestamp;
  }

  public int getRequestCount() {
    return requestCount;
  }

  public void setRequestCount(int requestCount) {
    this.requestCount = requestCount;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }
}
