package com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala;

public class RicartAgrawalaRequest {
  private long timestamp;
  private int requestCount;
  private String operation;

  public RicartAgrawalaRequest() {

  }

  public RicartAgrawalaRequest(long timestamp, int requestCount, String operation) {
    this.timestamp = timestamp;
    this.requestCount = requestCount;
    this.operation = operation;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
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
