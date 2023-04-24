package com.example.distributedsystems.distributed.systems.dsalgo.paxos;

import com.example.distributedsystems.distributed.systems.coordinator.RestService;
import com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala.RicartAgrawalaHandler;
import com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala.RicartAgrawalaRelease;
import com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala.RicartAgrawalaRequest;
import com.example.distributedsystems.distributed.systems.node.NodeRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class PaxosController {

  private static final Logger logger = LoggerFactory.getLogger(PaxosController.class);

  private static final int AWAIT_TERMINATION_SECONDS = 10;
  private static final int THREAD_POOL_SIZE = 10;


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

  private void preparePhase(PaxosTransaction paxosTransaction) {
    logger.info("Initiating PAXOS Prepare");
    Set<String> allNodes = nodeRegistry.getActiveNodes();
    String pid = System.currentTimeMillis() + serverPort + "";
    System.out.println("pid is: " + pid);
    paxosTransaction.setProposalId(System.currentTimeMillis() + serverPort);
    ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    for (String url : allNodes) {
      executor.execute(() -> {
        LinkedHashMap<String, Object> receivedPromise = (LinkedHashMap<String, Object>) restService.post(url + "/paxos/prepare", paxosTransaction).getBody();
        Promise promise = new Promise((boolean) receivedPromise.get("didPromise"), (long) receivedPromise.get("proposalId"));
        if (promise != null && promise.isDidPromise()) {
          promiseAccepted.incrementAndGet();
        }
      });
    }

    executor.shutdown();
    try {
      executor.awaitTermination(AWAIT_TERMINATION_SECONDS, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    logger.info("Number of promises received: " + promiseAccepted.get() + ", Total number of nodes:: " + allNodes.size());
    // Failed to reach consensus
    if(promiseAccepted.get() <= allNodes.size()/2){
      logger.error("Failed to reach consensus for the proposal: " + paxosTransaction.getProposalId() + " in prepare phase");
    } else {
      //Call accept phase
      acceptPhase(paxosTransaction);
    }
  }

  private void acceptPhase(PaxosTransaction paxosTransaction) {
    logger.info("Initiating PAXOS Accept");
    Set<String> allNodes = nodeRegistry.getActiveNodes();
    ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    for (String url : allNodes) {
      executor.execute(() -> {
        long acceptedTID = (long)restService.post(url + "/paxos/accept", paxosTransaction).getBody();
        System.out.println("accepted id is: " + acceptedTID);
        if(acceptedTID != Long.MIN_VALUE){
          nodesAccepted.incrementAndGet();
        }
      });
    }
    executor.shutdown(); // To execute the above tasks, which is to send the commit message to all the replica together and not one after other.
    try {
      executor.awaitTermination(AWAIT_TERMINATION_SECONDS, TimeUnit.SECONDS); // We perform this blocking operation to finish the execution of the above tasks
      logger.info("Number of nodes accepted: " + nodesAccepted.get() + ", Total number of nodes: " + allNodes.size());
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    // Failed to reach consensus
    if(nodesAccepted.get() <= allNodes.size()/2){
      System.out.println("failed to reach consensus for the transaction: " + paxosTransaction.getTransactionId() + " in accept");
    } else {
      // Call learn phase
      learnPhase(paxosTransaction);
    }
  }

  private void learnPhase(PaxosTransaction paxosTransaction) {
    logger.info("Initiating PAXOS Learn");
    Set<String> allNodes = nodeRegistry.getActiveNodes();

    ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    // consensus achieved all nodes accepted, now we go to learn phase
    for (String url: allNodes) {
      executor.execute(() -> {
        // learning phase
        Boolean ans = (Boolean) restService.post(url + "/paxos/learn", paxosTransaction).getBody();
      });
    }

    executor.shutdown(); // To execute the above tasks, which is to send the commit message to all the replica together and not one after other.
    try {
      executor.awaitTermination(AWAIT_TERMINATION_SECONDS, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void requestLocksFromAllInstances(PaxosScenario operation) {
    logger.info("Initiating Ricart Agrawala request lock");

    Set<String> nodeAddresses = nodeRegistry.getActiveNodes();

    while (true) {
      long timestamp = System.currentTimeMillis();
      int requestCountForOperation = ricartAgrawalaHandler.getAndIncrementRequestCount(operation.toString());
      RicartAgrawalaRequest request = new RicartAgrawalaRequest(timestamp, requestCountForOperation, operation.toString());

      boolean allLocksAcquired = nodeAddresses.stream()
              .map(nodeAddress -> nodeAddress + "/ricartAgrawala/request/" + operation.toString())
              .map(url -> restService.post(url, request))
              .allMatch(response -> Boolean.TRUE.equals(response.getBody()));

      if (allLocksAcquired) {
        logger.info("All locks acquired");
        break;
      }

      try {
        Thread.sleep(AWAIT_TERMINATION_SECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void releaseLocksFromAllInstances(PaxosScenario operation) {
    logger.info("Initiating Ricart Agrawala Release lock phase");

    Set<String> nodeAddresses = nodeRegistry.getActiveNodes();
    RicartAgrawalaRelease release = new RicartAgrawalaRelease(operation.toString());

    nodeAddresses.stream()
            .map(nodeAddress -> nodeAddress + "/ricartAgrawala/release/" + operation.toString())
            .forEach(url -> restService.post(url, release));
  }

  public ResponseEntity<Boolean> propose(@RequestBody PaxosTransaction paxosTransaction) {
    try {
      logger.info("Proposing transaction with proposal id: " + paxosTransaction.getProposalId());

      // Ricart-Agrawala Algorithm - Request phase
      requestLocksFromAllInstances(paxosTransaction.getScenario());

      // Paxos Algorithm
      preparePhase(paxosTransaction);

      // Ricart-Agrawala Algorithm - Release phase
      releaseLocksFromAllInstances(paxosTransaction.getScenario());

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return new ResponseEntity<>(true, HttpStatus.OK);
  }
}
