package com.example.distributedsystems.distributed.systems.dsalgo.vectortimestamps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A REST controller responsible for handling requests to update vector timestamps.
 */
@RestController
public class VectorTimestampController {

  private final VectorTimestampService vectorTimestampService;

  /**
   * Constructor with dependency injection.
   *
   * @param vectorTimestampService The service responsible for managing vector timestamps.
   */
  @Autowired
  public VectorTimestampController(VectorTimestampService vectorTimestampService) {
    this.vectorTimestampService = vectorTimestampService;
  }

  /**
   * Handles POST requests to update the vector timestamps for the current active nodes.
   *
   * @return An HTTP response indicating the result of the operation.
   */
  @PostMapping("/vectorTimestamps/update")
  public ResponseEntity<String> updateVectorTimestampsForCurrentActiveNodes() {
    vectorTimestampService.updateVectorTimestampsForCurrentActiveNodes();
    return new ResponseEntity<>("Vector timestamps updated for current active nodes", HttpStatus.OK);
  }
}
