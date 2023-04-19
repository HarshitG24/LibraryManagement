package com.example.distributedsystems.distributed.systems.controller;


import com.example.distributedsystems.distributed.systems.model.CreateUserRequest;
import com.example.distributedsystems.distributed.systems.model.User;
import com.example.distributedsystems.distributed.systems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/authenticate")
    public ResponseEntity<User> getUserByUsernameAnsPassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        User user = userService.getUserByUsernameAndPassword(username, password);
        System.out.println("users:" + user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserByUserId(@PathVariable Long userId) {
        User user = userService.getUserbyUserId(userId);
        System.out.println("users:" + user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Object> createUser(@RequestBody CreateUserRequest request) {
        User.Address address = new User.Address(request.getAddress1(), request.getAddress2(), request.getCity(), request.getState(), request.getZipcode());
        User user = new User(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword(), request.getUsername(), request.getPhone(), address);
        userService.createUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
