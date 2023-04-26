package com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RicartAgrawalaRelease {
  private String operation;
  private ConcurrentHashMap<String, AtomicInteger> vectorTimestamp;

  public RicartAgrawalaRelease() {
  }

  public RicartAgrawalaRelease(String operation, ConcurrentHashMap<String, AtomicInteger> vectorTimestamp) {
    this.operation = operation;
    this.vectorTimestamp = vectorTimestamp;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public ConcurrentHashMap<String, AtomicInteger> getVectorTimestamp() {
    return vectorTimestamp;
  }

  public void setVectorTimestamp(ConcurrentHashMap<String, AtomicInteger> vectorTimestamp) {
    this.vectorTimestamp = vectorTimestamp;
  }
}
