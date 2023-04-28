package com.example.distributedsystems.distributed.systems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Main entry point of the application
 */
@SpringBootApplication
@EntityScan(basePackages = {"com.example.distributedsystems.distributed.systems.model"})
public class DistributedSystemsApplication {

  public static void main(String[] args) {
    SpringApplication.run(DistributedSystemsApplication.class, args);
  }
}
