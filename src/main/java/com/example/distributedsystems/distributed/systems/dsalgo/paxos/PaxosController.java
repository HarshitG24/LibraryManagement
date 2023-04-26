package com.example.distributedsystems.distributed.systems.dsalgo.paxos;

import com.example.distributedsystems.distributed.systems.coordinator.RestService;
import com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala.RicartAgrawalaReply;
import com.example.distributedsystems.distributed.systems.dsalgo.vectortimestamps.VectorTimestampService;
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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PaxosController {

  private static final Logger logger = LoggerFactory.getLogger(PaxosController.class);

  private static final int AWAIT_TERMINATION_SECONDS = 10;
  private static final int THREAD_POOL_SIZE = 10;


  @Value("${server.port}")
  private int serverPort;

  @Autowired
  RestService restService;

//  @Autowired
//  NodeManager nodeManager;

  @Autowired
  NodeRegistry nodeRegistry;

  AtomicInteger promiseAccepted = new AtomicInteger(0);
  AtomicInteger nodesAccepted = new AtomicInteger(0);

  private final RicartAgrawalaHandler ricartAgrawalaHandler;
  private final VectorTimestampService vectorTimestampService;



  @Autowired
  public PaxosController(RicartAgrawalaHandler ricartAgrawalaHandler, VectorTimestampService vectorTimestampService) {
    this.ricartAgrawalaHandler = ricartAgrawalaHandler;
    this.vectorTimestampService = vectorTimestampService;
  }

  private void preparePhase(PaxosTransaction paxosTransaction) {
    logger.info("Initiating PAXOS Prepare");
    promiseAccepted.set(0); // Reset promiseAccepted to 0
    Set<String> allNodes = nodeRegistry.getActiveNodes();
//    String pid = System.currentTimeMillis() + serverPort + "";
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
    nodesAccepted.set(0); // Reset nodesAccepted to 0
    Set<String> allNodes = nodeRegistry.getActiveNodes();
    ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    for (String url : allNodes) {
      executor.execute(() -> {
        long acceptedTID = (long)restService.post(url + "/paxos/accept", paxosTransaction).getBody();
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
    try {
      Set<String> nodeAddresses = nodeRegistry.getActiveNodes();
      InetAddress address = InetAddress.getLocalHost();
      String currentNodeAddress = "http://" + address.getHostAddress() + ":" + serverPort;
      nodeAddresses.remove(currentNodeAddress); // Remove the current node's address from the list

      while (true) {
        vectorTimestampService.incrementVectorTimestamp(operation.toString(), currentNodeAddress);
        ConcurrentHashMap<String, AtomicInteger> vectorTimestamp = vectorTimestampService.getCurrentVectorTimestamp(operation.toString());
        logger.info("Current vector timestamp for " + operation + ": " + vectorTimestamp);
        int requestCountForOperation = ricartAgrawalaHandler.getAndIncrementRequestCount(operation.toString());
        RicartAgrawalaRequest request = new RicartAgrawalaRequest(vectorTimestamp, requestCountForOperation, operation.toString());

        List<RicartAgrawalaReply> replies = nodeAddresses.stream()
                .map(nodeAddress -> nodeAddress + "/ricartAgrawala/request/" + operation.toString())
                .map(url -> restService.post(url, request, RicartAgrawalaReply.class))
                .collect(Collectors.toList());

        boolean allLocksAcquired = replies.stream().allMatch(RicartAgrawalaReply::isGranted);


        if (allLocksAcquired) {
          logger.info("All locks acquired");
          // Update the vector timestamp of the current node
          replies.forEach(reply -> vectorTimestampService.updateVectorTimestamps(operation.toString(), reply.getVectorTimestamp()));
          break;
        }

        try {
          Thread.sleep(AWAIT_TERMINATION_SECONDS);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } catch (UnknownHostException e) {
      logger.error("Exception: " + e);
    }
  }

  private void releaseLocksFromAllInstances(PaxosScenario operation) {
    logger.info("Initiating Ricart Agrawala Release lock phase");
    try {
      Set<String> nodeAddresses = nodeRegistry.getActiveNodes();
      InetAddress address = InetAddress.getLocalHost();
      String currentNodeAddress = "http://" + address.getHostAddress() + ":" + serverPort;
      nodeAddresses.remove(currentNodeAddress); // Remove the current node's address from the list
      vectorTimestampService.incrementVectorTimestamp(operation.toString(), currentNodeAddress);
      ConcurrentHashMap<String, AtomicInteger> vectorTimestamp = vectorTimestampService.getCurrentVectorTimestamp(operation.toString());
      logger.info("Current vector timestamp for " + operation + ": " + vectorTimestamp);
      RicartAgrawalaRelease release = new RicartAgrawalaRelease(operation.toString(), vectorTimestamp);

      nodeAddresses.stream()
              .map(nodeAddress -> nodeAddress + "/ricartAgrawala/release/" + operation.toString())
              .map(url -> restService.post(url, release, RicartAgrawalaReply.class))
              .forEach(reply -> vectorTimestampService.updateVectorTimestamps(operation.toString(), reply.getVectorTimestamp()));
    } catch (UnknownHostException e) {
      logger.error("Exception: " + e);
    }
  }

  public ResponseEntity<Boolean> propose(@RequestBody PaxosTransaction paxosTransaction) {
    try {
      paxosTransaction.setProposalId(System.currentTimeMillis() + serverPort);
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
