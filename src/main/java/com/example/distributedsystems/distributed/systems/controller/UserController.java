package com.example.distributedsystems.distributed.systems.controller;


import com.example.distributedsystems.distributed.systems.model.User;
import com.example.distributedsystems.distributed.systems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUserByEmail")
    public ResponseEntity<User> getUserByEmail(@RequestBody String... data) {
        User user = userService.getUserByEmailAndPassword(data[0],data[1]);
        System.out.println("users:" + user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/getUserbyUserId")
    public ResponseEntity<User> getUserByUserId(@RequestBody Long user_id) {
        User user = userService.getUserbyUserId(user_id);
        System.out.println("users:" + user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/createUser")
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        userService.createUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
