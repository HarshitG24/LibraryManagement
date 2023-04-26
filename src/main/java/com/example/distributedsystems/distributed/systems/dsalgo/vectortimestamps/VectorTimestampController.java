package com.example.distributedsystems.distributed.systems.dsalgo.vectortimestamps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VectorTimestampController {

  private final VectorTimestampService vectorTimestampService;

  @Autowired
  public VectorTimestampController(VectorTimestampService vectorTimestampService) {
    this.vectorTimestampService = vectorTimestampService;
  }

  @PostMapping("/vectorTimestamps/update")
  public ResponseEntity<String> updateVectorTimestampsForCurrentActiveNodes() {
    vectorTimestampService.updateVectorTimestampsForCurrentActiveNodes();
    return new ResponseEntity<>("Vector timestamps updated for current active nodes", HttpStatus.OK);
  }
}
