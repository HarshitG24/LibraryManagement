package com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RicartAgrawalaHandler {
  private final ConcurrentHashMap<String, AtomicBoolean> operationLocks;
  private final ConcurrentHashMap<String, AtomicInteger> operationRequestCounts;
  private final ConcurrentHashMap<String, AtomicLong> operationTimestamps;
  private final ConcurrentHashMap<String, Queue<RicartAgrawalaRequest>> operationWaitingQueues;

  public RicartAgrawalaHandler() {
    operationLocks = new ConcurrentHashMap<>();
    operationRequestCounts = new ConcurrentHashMap<>();
    operationTimestamps = new ConcurrentHashMap<>();
    operationWaitingQueues = new ConcurrentHashMap<>();

    // Initialize for each operation
    String[] operations = {"createTransaction", "maxBookReturned", "addBookToCart", "deleteCart", "deleteBookFromCart"};
    for (String operation : operations) {
      operationLocks.put(operation, new AtomicBoolean(false));
      operationRequestCounts.put(operation, new AtomicInteger(0));
      operationTimestamps.put(operation, new AtomicLong(0));
      operationWaitingQueues.put(operation, new LinkedList<>());
    }
  }

  public boolean request(String operation, RicartAgrawalaRequest request) {
    AtomicBoolean lock = operationLocks.get(operation);
    AtomicInteger requestCount = operationRequestCounts.get(operation);
    AtomicLong timestamp = operationTimestamps.get(operation);
    Queue<RicartAgrawalaRequest> waitingQueue = operationWaitingQueues.get(operation);

    synchronized (lock) {
      // If the lock is not acquired, grant access immediately
      if (!lock.get()) {
        lock.set(true);
        requestCount.set(request.getRequestCount());
        timestamp.set(request.getTimestamp());
        return true;
      } else {
        // Check if the incoming request has higher priority
        boolean hasHigherPriority = (request.getTimestamp() < timestamp.get())
                || (request.getTimestamp() == timestamp.get() && request.getRequestCount() < requestCount.get());

        // If the request has higher priority, grant access and update the request count and timestamp
        if (hasHigherPriority) {
          lock.set(true);
          requestCount.set(request.getRequestCount());
          timestamp.set(request.getTimestamp());
          return true;
        } else {
          // Add the request to the waiting queue
          waitingQueue.add(request);
          return false;
        }
      }
    }
  }

  public void release(String operation, RicartAgrawalaRelease release) {
    AtomicBoolean lock = operationLocks.get(operation);
    Queue<RicartAgrawalaRequest> waitingQueue = operationWaitingQueues.get(operation);

    synchronized (lock) {
      lock.set(false);

      // Grant access to the next waiting request, if any
      if (!waitingQueue.isEmpty()) {
        RicartAgrawalaRequest nextRequest = waitingQueue.poll();
        lock.set(true);
      }

      lock.notifyAll();
    }
  }
}
