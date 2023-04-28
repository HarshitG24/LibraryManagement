package com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala;

import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosScenario;
import com.example.distributedsystems.distributed.systems.dsalgo.vectortimestamps.VectorTimestampService;
import com.example.distributedsystems.distributed.systems.node.NodeRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handles the request and release operations for the Ricart-Agrawala distributed mutual exclusion algorithm.
 */
@Component
public class RicartAgrawalaHandler {

  private static final Logger logger = LoggerFactory.getLogger(RicartAgrawalaHandler.class);
  private final VectorTimestampService vectorTimestampService;
  private final ConcurrentHashMap<String, AtomicBoolean> operationLocks;
  private final ConcurrentHashMap<String, AtomicInteger> operationRequestCounts;
  private final ConcurrentHashMap<String, Queue<RicartAgrawalaRequest>> operationWaitingQueues;
  NodeRegistry nodeRegistry;
  private Map<String, ConcurrentHashMap<String, AtomicInteger>> operationVectorTimestamps;

  /**
   * Constructs a new RicartAgrawalaHandler with the specified NodeRegistry and VectorTimestampService.
   *
   * @param nodeRegistry The NodeRegistry for managing nodes in the system.
   * @param vectorTimestampService The VectorTimestampService for managing vector timestamps.
   */
  public RicartAgrawalaHandler(NodeRegistry nodeRegistry, VectorTimestampService vectorTimestampService) {
    this.nodeRegistry = nodeRegistry;
    this.vectorTimestampService = vectorTimestampService;
    operationLocks = new ConcurrentHashMap<>();
    operationRequestCounts = new ConcurrentHashMap<>();
    operationWaitingQueues = new ConcurrentHashMap<>();

    // Initialize for each operation
    List<String> operations = Stream.of(PaxosScenario.values())
            .map(PaxosScenario::name)
            .collect(Collectors.toList());
    for (String operation : operations) {
      operationLocks.put(operation, new AtomicBoolean(false));
      operationRequestCounts.put(operation, new AtomicInteger(0));
      operationWaitingQueues.put(operation, new LinkedList<RicartAgrawalaRequest>());
      this.operationVectorTimestamps = vectorTimestampService.getOperationVectorTimestamps();
    }
  }

  /**
   * Processes a lock request for the specified operation using the Ricart-Agrawala algorithm.
   *
   * @param operation The operation requested lock for.
   * @param request The RicartAgrawalaRequest containing the request details.
   * @param nodeAddress The address of the requesting node.
   * @return A RicartAgrawalaReply containing the result of the request.
   */
  public RicartAgrawalaReply request(String operation, RicartAgrawalaRequest request, String nodeAddress) {
    AtomicBoolean lock = operationLocks.get(operation);
    AtomicInteger requestCount = operationRequestCounts.get(operation);
    Queue<RicartAgrawalaRequest> waitingQueue = operationWaitingQueues.get(operation);

    synchronized (lock) {
      // Increment the current instance's timestamp
      vectorTimestampService.incrementVectorTimestamp(operation, nodeAddress);
      // If the lock is not acquired, grant access immediately
      if (!lock.get()) {
        requestCount.set(request.getRequestCount());
        vectorTimestampService.updateVectorTimestamps(operation, request.getVectorTimestamp());
        return new RicartAgrawalaReply(true, operationVectorTimestamps.get(operation));
      } else {
        // Check whether the incoming request has a higher priority than the current lock holder based on vector timestamp
        boolean hasHigherPriority = compareVectorTimestamps(request.getVectorTimestamp(), operationVectorTimestamps.get(operation));

        // If the incoming request has higher priority, grant access and update the request count and timestamp.
        if (hasHigherPriority) {
          requestCount.set(request.getRequestCount());
          vectorTimestampService.updateVectorTimestamps(operation, request.getVectorTimestamp());
          return new RicartAgrawalaReply(true, operationVectorTimestamps.get(operation));
        } else {
          // Add the incoming request in the waiting queue
          waitingQueue.add(request);
          return new RicartAgrawalaReply(false, operationVectorTimestamps.get(operation));
        }
      }
    }
  }

  /**
   * Processes a lock release for the specified operation using the Ricart-Agrawala algorithm.
   *
   * @param operation The operation to release lock for.
   * @param release The RicartAgrawalaRelease containing the release details.
   * @param nodeAddress The address of the releasing node.
   * @return A RicartAgrawalaReply containing the result of the release.
   */
  public RicartAgrawalaReply release(String operation, RicartAgrawalaRelease release, String nodeAddress) {
    RicartAgrawalaReply reply = null;
    AtomicBoolean lock = operationLocks.get(operation);
    Queue<RicartAgrawalaRequest> waitingQueue = operationWaitingQueues.get(operation);

    synchronized (lock) {
      // Increment the current instance's timestamp
      vectorTimestampService.incrementVectorTimestamp(operation, nodeAddress);

      lock.set(false);

      // Update the vector timestamp
      vectorTimestampService.updateVectorTimestamps(operation, release.getVectorTimestamp());

      // If there is any waiting request in the queue, grant access to the next waiting request.
      if (!waitingQueue.isEmpty()) {
        RicartAgrawalaRequest nextRequest = waitingQueue.poll();
        if (compareVectorTimestamps(nextRequest.getVectorTimestamp(), operationVectorTimestamps.get(operation))) {
          lock.set(true);
          vectorTimestampService.updateVectorTimestamps(operation, nextRequest.getVectorTimestamp());
          reply = new RicartAgrawalaReply(true, operationVectorTimestamps.get(operation));
        }
      }

      lock.notifyAll();
    }
    if (reply == null) {
      reply = new RicartAgrawalaReply(false, operationVectorTimestamps.get(operation));
    }
    return reply;
  }

  /**
   * Compares the incoming vector timestamp with the local vector timestamp to determine priority.
   *
   * @param incomingTimestamp The incoming vector timestamp.
   * @param localTimestamp The local vector timestamp.
   * @return True if the incoming vector timestamp has a higher priority, otherwise false.
   */
  private boolean compareVectorTimestamps(ConcurrentHashMap<String, AtomicInteger> incomingTimestamp, ConcurrentHashMap<String, AtomicInteger> localTimestamp) {
    boolean allLessThanOrEqual = true;
    boolean atLeastOneLess = false;

    for (Map.Entry<String, AtomicInteger> entry : incomingTimestamp.entrySet()) {
      String nodeId = entry.getKey();
      int incomingValue = entry.getValue().get();
      int localValue = localTimestamp.get(nodeId).get();

      if (incomingValue > localValue) {
        allLessThanOrEqual = false;
        break;
      } else if (incomingValue < localValue) {
        atLeastOneLess = true;
      }
    }

    return allLessThanOrEqual && atLeastOneLess;
  }

  /**
   * Gets the request count for the specified operation and increments it.
   *
   * @param operation The operation for which to get and increment the request count.
   * @return The request count before incrementing.
   */
  public int getAndIncrementRequestCount(String operation) {
    AtomicInteger requestCount = operationRequestCounts.get(operation);
    return requestCount.getAndIncrement();
  }
}
