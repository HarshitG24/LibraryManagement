package com.example.distributedsystems.distributed.systems.controller;


import com.example.distributedsystems.distributed.systems.dsalgo.twopc.TwoPCController;
import com.example.distributedsystems.distributed.systems.model.Response;
import com.example.distributedsystems.distributed.systems.model.user.CreateUserRequest;
import com.example.distributedsystems.distributed.systems.model.user.User;
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
@RequestMapping("/user")
public class UserController extends TwoPCController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Get all users request received.");
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/authenticate")
    public ResponseEntity<User> getUserByUsernameAnsPassword(@RequestParam String username, @RequestParam String password) {
        logger.info("Authenticate user request received for username: " + username + ", password: " + password);
        User user = userService.getUserByUsernameAndPassword(username, password);
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserByUserId(@PathVariable Long userId) {
        logger.info("Get user received for UserId: " + userId);
        User user = userService.getUserbyUserId(userId);
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/createUser")
    public ResponseEntity<Response> createUser(@RequestBody CreateUserRequest request) {
        logger.info("Create user request received. UserRequest: " + request);
        User.Address address = new User.Address(request.getAddress1(), request.getAddress2(), request.getCity(), request.getState(), request.getZipcode());
        User user = new User(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword(), request.getUsername(), request.getPhone(), address);
        ResponseEntity<Response> createUserResponse;
        try{
            createUserResponse = performTransaction(user);
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

        return createUserResponse;
    }
}
