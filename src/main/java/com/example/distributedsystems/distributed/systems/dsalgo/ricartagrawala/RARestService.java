package com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RARestService {

  private final RicartAgrawalaHandler ricartAgrawalaHandler;

  public RARestService() {
    ricartAgrawalaHandler = new RicartAgrawalaHandler();
  }

  @PostMapping("/ricartAgrawala/request/{operation}")
  public ResponseEntity<Boolean> requestRicartAgrawala(@PathVariable String operation, @RequestBody RicartAgrawalaRequest request) {
    boolean granted = ricartAgrawalaHandler.request(operation, request);
    return ResponseEntity.ok(granted);
  }

  @PostMapping("/ricartAgrawala/release/{operation}")
  public ResponseEntity<Void> releaseRicartAgrawala(@PathVariable String operation, @RequestBody RicartAgrawalaRelease release) {
    ricartAgrawalaHandler.release(operation, release);
    return ResponseEntity.ok().build();
  }
}
