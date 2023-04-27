package com.example.distributedsystems.distributed.systems.service;

import com.example.distributedsystems.distributed.systems.dsalgo.twopc.TwoPCPromise;
import com.example.distributedsystems.distributed.systems.model.Response;
import com.example.distributedsystems.distributed.systems.model.user.User;
import com.example.distributedsystems.distributed.systems.repository.UserInterface;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserInterface userInterface;

    public List<User> getAllUsers() {
        return (List<User>) userInterface.findAll();
    }
    public User getUserbyUserId(Long userId) {
        return  userInterface.getUserByUserId(userId);
    }

    @Transactional
    public TwoPCPromise createUser(User user) {
        TwoPCPromise promise;
        if (userInterface.findByEmail(user.getEmail()) != null) {

            promise = new TwoPCPromise(false,"Email already exist");
            return promise;

        }
        if (userInterface.findByUsername(user.getUsername()) != null) {
            promise = new TwoPCPromise(false,"Username already exist");
            return promise;

        }
        userInterface.save(user);
        promise = new TwoPCPromise(true,"User added successfully");
        return promise;
    }

    public User getUserByUsernameAndPassword(String username, String password) {
        return userInterface.getUserByUsernameAndPassword(username,password);
    }
}
