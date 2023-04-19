package com.example.distributedsystems.distributed.systems.controller;


import com.example.distributedsystems.distributed.systems.dsalgo.twopc.TwoPCController;
import com.example.distributedsystems.distributed.systems.model.CreateUserRequest;
import com.example.distributedsystems.distributed.systems.model.Employee;
import com.example.distributedsystems.distributed.systems.model.User;
import com.example.distributedsystems.distributed.systems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/user")
public class UserController extends TwoPCController {

    @Autowired
    private UserService userService;

    @GetMapping("/authenticate")
    public ResponseEntity<User> getUserByUsernameAnsPassword(@RequestParam String username, @RequestParam String password) {
        System.out.println(username);
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

    @PostMapping("")
    public ResponseEntity<Object> createUser(@RequestBody CreateUserRequest request) {
        User.Address address = new User.Address(request.getAddress1(), request.getAddress2(), request.getCity(), request.getState(), request.getZipcode());
        User user = new User(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword(), request.getUsername(), request.getPhone(), address);
        userService.createUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/createUser")
    public ResponseEntity<Object> createEmployee(@RequestBody User e) {


//        employeeService.createEmployee(e);
        performTransaction(e);
        return new ResponseEntity<>(HttpStatus.OK);
//        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
