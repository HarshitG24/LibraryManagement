package com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RicartAgrawalaReply {
  private boolean granted;
  private ConcurrentHashMap<String, AtomicInteger> vectorTimestamp;

  public RicartAgrawalaReply(boolean granted, ConcurrentHashMap<String, AtomicInteger> vectorTimestamp) {
    this.granted = granted;
    this.vectorTimestamp = vectorTimestamp;
  }

  public boolean isGranted() {
    return granted;
  }

  public void setGranted(boolean granted) {
    this.granted = granted;
  }

  public ConcurrentHashMap<String, AtomicInteger> getVectorTimestamp() {
    return vectorTimestamp;
  }

  public void setVectorTimestamp(ConcurrentHashMap<String, AtomicInteger> vectorTimestamp) {
    this.vectorTimestamp = vectorTimestamp;
  }
}
