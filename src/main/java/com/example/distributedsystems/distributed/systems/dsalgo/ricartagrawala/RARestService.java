package com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala;

import com.example.distributedsystems.distributed.systems.dsalgo.vectortimestamps.VectorTimestampService;
import com.example.distributedsystems.distributed.systems.node.NodeManager;
import com.example.distributedsystems.distributed.systems.node.NodeRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RARestService {
  @Autowired
  private NodeManager nodeManager;

  private static final Logger logger = LoggerFactory.getLogger(RARestService.class);

  private final RicartAgrawalaHandler ricartAgrawalaHandler;

  @Autowired
  public RARestService(NodeRegistry nodeRegistry, VectorTimestampService vectorTimestampService) {
    this.ricartAgrawalaHandler = new RicartAgrawalaHandler(nodeRegistry, vectorTimestampService);
  }

  @PostMapping("/ricartAgrawala/request/{operation}")
  public ResponseEntity<RicartAgrawalaReply> requestRicartAgrawala(@PathVariable String operation, @RequestBody RicartAgrawalaRequest request) {
    logger.info("Critical section lock requested");
    RicartAgrawalaReply ricartAgrawalaReply = ricartAgrawalaHandler.request(operation, request, nodeManager.getNodeAddress());
    logger.info("Lock granted : " + ricartAgrawalaReply.isGranted());

    return ResponseEntity.ok(ricartAgrawalaReply);
  }

  @PostMapping("/ricartAgrawala/release/{operation}")
  public ResponseEntity<RicartAgrawalaReply> releaseRicartAgrawala(@PathVariable String operation, @RequestBody RicartAgrawalaRelease release) {
    RicartAgrawalaReply ricartAgrawalaReply = ricartAgrawalaHandler.release(operation, release, nodeManager.getNodeAddress());
    logger.info("Lock released: " + ricartAgrawalaReply.isGranted());

    return ResponseEntity.ok(ricartAgrawalaReply);
  }
}
