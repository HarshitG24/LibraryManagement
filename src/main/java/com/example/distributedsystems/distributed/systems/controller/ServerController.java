package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.model.Server;
import com.example.distributedsystems.distributed.systems.model.user.User;
import com.example.distributedsystems.distributed.systems.service.EmployeeService;
import com.example.distributedsystems.distributed.systems.service.ServerService;
import com.example.distributedsystems.distributed.systems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/server")

public class ServerController {

    @Autowired
    private ServerService serverService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @GetMapping("/allServers")
    public ResponseEntity<List<Server>> listServers() {
        List<Server> servers = serverService.listAllServer();
        System.out.println("users are: " + servers);
        return new ResponseEntity<>(servers, HttpStatus.OK);
    }

    @GetMapping("/ack")
    public ResponseEntity<List<Server>> getAck() {
        List<Server> servers = serverService.listAllServer();
        System.out.println("users are: " + servers);
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
//        employeeService.createEmployee(new Employee("harshit", "mihir"));
//        userService.createUser(new User("fn", "ln", "qaz@eec.com", "p", "john", "7899", new User.Address("600", "california", "San Francisco", "CA", "94108")));
        userService.createUser(user);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
