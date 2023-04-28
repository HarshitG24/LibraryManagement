package com.example.distributedsystems.distributed.systems.dsalgo.vectortimestamps;

import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosScenario;
import com.example.distributedsystems.distributed.systems.node.NodeRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A service responsible for managing vector timestamps in a distributed system.
 */
@Service
public class VectorTimestampService {
  private static final Logger logger = LoggerFactory.getLogger(VectorTimestampService.class);

  private final NodeRegistry nodeRegistry;

  private final ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> operationVectorTimestamps;

  /**
   * Constructor for the VectorTimestampService class.
   *
   * @param nodeRegistry The NodeRegistry used for getting information about active nodes.
   */
  public VectorTimestampService(NodeRegistry nodeRegistry) {
    this.nodeRegistry = nodeRegistry;
    // Initialize for each operation
    this.operationVectorTimestamps = new ConcurrentHashMap<>();
    // Initialize for each operation
    List<String> operations = Stream.of(PaxosScenario.values())
            .map(PaxosScenario::name)
            .collect(Collectors.toList());
    for (String operation : operations) {
      if (!operationVectorTimestamps.containsKey(operation)) {
        operationVectorTimestamps.put(operation, new ConcurrentHashMap<>());
      }
    }
  }

  /**
   * Retrieves the current vector timestamp for the given operation.
   *
   * @param operation The operation for which to retrieve the vector timestamp.
   * @return The current vector timestamp for the given operation.
   */
  public synchronized ConcurrentHashMap<String, AtomicInteger> getCurrentVectorTimestamp(String operation) {
    //Get current timestamp for operation
    ConcurrentHashMap<String, AtomicInteger> vectorTimestamp = operationVectorTimestamps.get(operation);
    if (vectorTimestamp == null) {
      vectorTimestamp = new ConcurrentHashMap<>();
      for (String nodeAddress : this.nodeRegistry.getActiveNodes()) {
        vectorTimestamp.put(nodeAddress, new AtomicInteger(0));
      }
      operationVectorTimestamps.put(operation, vectorTimestamp);
    }

    ConcurrentHashMap<String, AtomicInteger> currentVectorTimestamp = new ConcurrentHashMap<>();
    for (String nodeAddress : vectorTimestamp.keySet()) {
      currentVectorTimestamp.put(nodeAddress, vectorTimestamp.get(nodeAddress));
    }
    return currentVectorTimestamp;
  }

  /**
   * Increments the vector timestamp for the given operation and node address.
   *
   * @param operation   The operation for which to increment the vector timestamp.
   * @param nodeAddress The node address for which to increment the vector timestamp.
   */
  public synchronized void incrementVectorTimestamp(String operation, String nodeAddress) {
    ConcurrentHashMap<String, AtomicInteger> vectorTimestamp = operationVectorTimestamps.get(operation);
    if (vectorTimestamp != null) {
      if (vectorTimestamp.containsKey(nodeAddress)) {
        vectorTimestamp.get(nodeAddress).set(vectorTimestamp.get(nodeAddress).incrementAndGet());
        operationVectorTimestamps.put(operation, vectorTimestamp);
      } else {
        logger.error("Invalid node address to update vector timestamp: " + nodeAddress);
        throw new IllegalArgumentException("Invalid node address: " + nodeAddress);
      }
    } else {
      logger.error("Invalid operation to update vector timestamp: " + nodeAddress);
      throw new IllegalArgumentException("Invalid operation: " + operation);
    }
    logger.info("Own vector timestamp increment for operation: " + operation + " Updated Vector timestamps: " + vectorTimestamp);
  }

  /**
   * Updates the vector timestamps for the given operation using the incoming timestamp.
   *
   * @param operation        The operation for which to update the vector timestamps.
   * @param incomingTimestamp The incoming timestamp used to update the vector timestamps.
   */
  public void updateVectorTimestamps(String operation, ConcurrentHashMap<String, AtomicInteger> incomingTimestamp) {
    ConcurrentHashMap<String, AtomicInteger> vectorTimestamp = operationVectorTimestamps.get(operation);
    for (Map.Entry<String, AtomicInteger> entry : incomingTimestamp.entrySet()) {
      String nodeId = entry.getKey();
      int incomingValue = entry.getValue().get();
      vectorTimestamp.get(nodeId).set(Math.max(incomingValue, vectorTimestamp.get(nodeId).get()));
    }
    operationVectorTimestamps.put(operation, vectorTimestamp);
    logger.info("Vector timestamp updated based on request timestamp for operation: " + operation + " Updated Vector timestamps: " + vectorTimestamp);
  }

  /**
   * Updates the vector timestamps for the currently active nodes in the system.
   */
  public synchronized void updateVectorTimestampsForCurrentActiveNodes() {
    for (String operation : operationVectorTimestamps.keySet()) {
      ConcurrentHashMap<String, AtomicInteger> currentTimestamps = operationVectorTimestamps.get(operation);

      // Remove entries for the nodes that are no longer active
      for (String nodeId : currentTimestamps.keySet()) {
        if (!nodeRegistry.getActiveNodes().contains(nodeId)) {
          currentTimestamps.remove(nodeId);
        }
      }

      // Add entries for the new active nodes
      for (String nodeId : nodeRegistry.getActiveNodes()) {
        if (!currentTimestamps.containsKey(nodeId)) {
          currentTimestamps.put(nodeId, new AtomicInteger(0));
        }
      }
    }
    logger.info("Updated vector timestamps for currently active node (A new must have joined or left the cluster). Active nodes: " + nodeRegistry.getActiveNodes());
    logger.info("Vector timestamps: " + operationVectorTimestamps.entrySet().stream()
            .map(entry -> "Operation: " + entry.getKey() + ", Timestamps: " + entry.getValue())
            .collect(Collectors.joining(", \n")));
  }

  /**
   * Prints all vector timestamps for logging purposes.
   */
  public void printAllVectorTimestamps() {
    logger.info("Vector timestamp. Vector timestamps: " + operationVectorTimestamps.entrySet().stream()
            .map(entry -> "Operation: " + entry.getKey() + ", Timestamps: " + entry.getValue())
            .collect(Collectors.joining(", \n")));
  }

  /**
   * Retrieves the operation vector timestamps.
   *
   * @return The operation vector timestamps.
   */
  public ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> getOperationVectorTimestamps() {
    return operationVectorTimestamps;
  }
}
