package com.example.distributedsystems.distributed.systems.dsalgo.paxos;

import com.example.distributedsystems.distributed.systems.coordinator.RestService;
import com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala.RicartAgrawalaHandler;
import com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala.RicartAgrawalaRelease;
import com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala.RicartAgrawalaRequest;
import com.example.distributedsystems.distributed.systems.node.NodeRegistry;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Paxos {

  @Value("${server.port}")
  private int serverPort;

  @Autowired
  RestService restService;

  @Autowired
  NodeRegistry nodeRegistry;

  @Autowired
  private RicartAgrawalaHandler ricartAgrawalaHandler;

//  @Autowired
//  private HazelcastInstance hazelcastInstance;
//
//  public void incrementRequestCount() {
//    IAtomicLong requestCount = hazelcastInstance.getCPSubsystem().getAtomicLong("requestCount");
//    requestCount.incrementAndGet();
//  }
//
//  public long getRequestCount() {
//    IAtomicLong requestCount = hazelcastInstance.getCPSubsystem().getAtomicLong("requestCount");
//    return requestCount.get();
//  }

  int promiseAccepted = 0;
  int nodesAccepted = 0;

  private void preparePhase(PaxosTransaction paxosTransaction, Set<String> allPorts) {
    System.out.println("Prepare phase");
    ExecutorService executor = Executors.newFixedThreadPool(10);

    for (String url : allPorts) {
      executor.execute(() -> {
        LinkedHashMap<String, Object> receivedPromise = (LinkedHashMap<String, Object>) restService.post(url + "/paxos/prepare", paxosTransaction).getBody();
        Promise promise = new Promise((boolean) receivedPromise.get("didPromise"), (long) receivedPromise.get("propsalId"));
        if (promise != null && promise.isDidPromise()) {
          promiseAccepted++;
        }
      });
    }

    executor.shutdown();
    try {
      executor.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    System.out.println("Number of promised got: " + promiseAccepted + ", allports: " + allPorts.size());
    // Failed to reach consensus
    if(promiseAccepted <= allPorts.size()/2){
      System.out.println("failed to reach consensus for the transaction: " + paxosTransaction.getTransactionId() + " in prepare phase");
    }
  }

  private void acceptPhase(PaxosTransaction paxosTransaction, Set<String> allPorts) {
    System.out.println("Accept phase");

    ExecutorService executor = Executors.newFixedThreadPool(10);

    for (String url : allPorts) {
      executor.execute(() -> {
        Long acceptedTID = (Long) restService.post(url + "/paxos/accept", paxosTransaction).getBody();
        if (acceptedTID != null) {
          nodesAccepted++;
        }
      });
    }
    System.out.println("number of nodes accepted: " + nodesAccepted + ", total: " + allPorts.size());
    executor.shutdown();
    try {
      executor.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void learnPhase(PaxosTransaction paxosTransaction, Set<String> allPorts) {
    System.out.println("Learn phase");

    ExecutorService executor = Executors.newFixedThreadPool(10);

    // consensus achieved all nodes accepted, now we go to learn phase
    for (String url: allPorts) {
      executor.execute(() -> {
        // learning phase
        restService.post(url + "/paxos/learn", paxosTransaction);
      });
    }

    executor.shutdown(); // To execute the above tasks, which is to send the commit message to all the replica together and not one after other.
    try {
      executor.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void requestLock(PaxosScenario operation) {
    System.out.println("Request lock phase");

    boolean lockAcquired = false;
    while (!lockAcquired) {
      long timestamp = System.currentTimeMillis();
      int requestCountForOperation = ricartAgrawalaHandler.getAndIncrementRequestCount(operation.toString());
      Boolean response = (Boolean) restService.post("http://localhost:" + serverPort + "/ricartAgrawala/request/" + operation.toString(), new RicartAgrawalaRequest(timestamp, requestCountForOperation, operation.toString())).getBody();
      lockAcquired = Boolean.TRUE.equals(response);
      System.out.println("Lock acquired: " + lockAcquired);

      if (!lockAcquired) {
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void releaseLock(PaxosScenario operation) {
    System.out.println("Release lock phase");
    restService.post("http://localhost:" + serverPort + "/ricartAgrawala/release/" + operation.toString(), new RicartAgrawalaRelease(operation.toString()));
  }

  public ResponseEntity<Boolean> propose(@RequestBody PaxosTransaction paxosTransaction) {
    try {
      Set<String> allPorts = nodeRegistry.getActiveNodes();

      // Ricart-Agrawala Algorithm - Request phase
      requestLock(paxosTransaction.getScenario());

      // Paxos Algorithm
      preparePhase(paxosTransaction, allPorts);
      acceptPhase(paxosTransaction, allPorts);
      learnPhase(paxosTransaction, allPorts);

      // Ricart-Agrawala Algorithm - Release phase
      releaseLock(paxosTransaction.getScenario());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return new ResponseEntity<>(true, HttpStatus.OK);
  }
}
