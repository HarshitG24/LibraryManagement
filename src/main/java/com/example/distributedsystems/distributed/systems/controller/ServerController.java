package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.model.Server;
import com.example.distributedsystems.distributed.systems.model.user.User;
import com.example.distributedsystems.distributed.systems.service.ServerService;
import com.example.distributedsystems.distributed.systems.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/server")

public class ServerController {
    private static final Logger logger = LoggerFactory.getLogger(ServerController.class);

    @Autowired
    private ServerService serverService;

    @Autowired
    private UserService userService;

    @GetMapping("/allServers")
    public ResponseEntity<List<Server>> listServers() {
        List<Server> servers = serverService.listAllServer();
        return new ResponseEntity<>(servers, HttpStatus.OK);
    }

    @GetMapping("/ack")
    public ResponseEntity<List<Server>> getAck() {
        List<Server> servers = serverService.listAllServer();
        return new ResponseEntity<>(servers, HttpStatus.OK);
    }

    // refactor
    @GetMapping("/cancommit")
    public ResponseEntity<Boolean> canCommit() {
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    // refactor
    @PostMapping("/docommit")
    public ResponseEntity<Boolean> doCommit(@RequestBody User user) {
        try {
            userService.createUser(user);
        } catch (IllegalArgumentException exception) {
            logger.info("Illegal Argument Exception: " + exception);
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
