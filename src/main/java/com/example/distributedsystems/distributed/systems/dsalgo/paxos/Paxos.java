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

  AtomicInteger promiseAccepted = new AtomicInteger(0);
  AtomicInteger nodesAccepted = new AtomicInteger(0);

  private void preparePhase(PaxosTransaction paxosTransaction, Set<String> allPorts) {
    System.out.println("Prepare phase");
    String pid = System.currentTimeMillis() + serverPort + "";
    System.out.println("pid is: " + pid);
    paxosTransaction.setProposalId(System.currentTimeMillis() + serverPort);
    ExecutorService executor = Executors.newFixedThreadPool(10);

    for (String url : allPorts) {
      executor.execute(() -> {
        LinkedHashMap<String, Object> receivedPromise = (LinkedHashMap<String, Object>) restService.post(url + "/paxos/prepare", paxosTransaction).getBody();
        Promise promise = new Promise((boolean) receivedPromise.get("didPromise"), (long) receivedPromise.get("propsalId"));
        if (promise != null && promise.isDidPromise()) {
          promiseAccepted.incrementAndGet();
        }
      });
    }

    executor.shutdown();
    try {
      executor.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    System.out.println("Number of promised got: " + promiseAccepted.get() + ", allports: " + allPorts.size());
    // Failed to reach consensus
    if(promiseAccepted.get() <= allPorts.size()/2){
      System.out.println("failed to reach consensus for the transaction: " + paxosTransaction.getTransactionId() + " in prepare phase");
    }
  }

  private void acceptPhase(PaxosTransaction paxosTransaction, Set<String> allPorts) {
    System.out.println("Accept phase");

    ExecutorService executor = Executors.newFixedThreadPool(10);

    for (String url : allPorts) {
      executor.execute(() -> {
        long acceptedTID = (long)restService.post(url + "/paxos/accept", paxosTransaction).getBody();
        System.out.println("accepted id is: " + acceptedTID);
        if(acceptedTID != Long.MIN_VALUE){
          nodesAccepted.incrementAndGet();
        }
      });
    }
    System.out.println("number of nodes accepted: " + nodesAccepted.get() + ", total: " + allPorts.size());
    executor.shutdown(); // To execute the above tasks, which is to send the commit message to all the replica together and not one after other.
    try {
      executor.awaitTermination(10, TimeUnit.SECONDS); // We perform this blocking operation to finish the execution of the above tasks
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

  private void requestLocksFromAllInstances(PaxosScenario operation) {
    System.out.println("Request lock phase");

    Set<String> allPorts = nodeRegistry.getActiveNodes();

    while (true) {
      long timestamp = System.currentTimeMillis();
      int requestCountForOperation = ricartAgrawalaHandler.getAndIncrementRequestCount(operation.toString());
      RicartAgrawalaRequest request = new RicartAgrawalaRequest(timestamp, requestCountForOperation, operation.toString());

      boolean allLocksAcquired = allPorts.stream()
              .map(port -> "http://localhost:" + port + "/ricartAgrawala/request/" + operation.toString())
              .map(url -> restService.post(url, request))
              .allMatch(response -> Boolean.TRUE.equals(response.getBody()));

      if (allLocksAcquired) {
        System.out.println("All locks acquired");
        break;
      }

      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void releaseLocksFromAllInstances(PaxosScenario operation) {
    System.out.println("Release lock phase");

    Set<String> allPorts = nodeRegistry.getActiveNodes();
    RicartAgrawalaRelease release = new RicartAgrawalaRelease(operation.toString());

    allPorts.stream()
            .map(port -> "http://localhost:" + port + "/ricartAgrawala/release/" + operation.toString())
            .forEach(url -> restService.post(url, release));
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
      requestLocksFromAllInstances(paxosTransaction.getScenario());

      // Paxos Algorithm
      preparePhase(paxosTransaction, allPorts);
      acceptPhase(paxosTransaction, allPorts);
      learnPhase(paxosTransaction, allPorts);

      // Ricart-Agrawala Algorithm - Release phase
      releaseLocksFromAllInstances(paxosTransaction.getScenario());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return new ResponseEntity<>(true, HttpStatus.OK);
  }
}
