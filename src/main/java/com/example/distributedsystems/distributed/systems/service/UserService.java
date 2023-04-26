package com.example.distributedsystems.distributed.systems.service;

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
    public void createUser(User user) {
        if (userInterface.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userInterface.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        userInterface.save(user);
    }

    public User getUserByUsernameAndPassword(String username, String password) {
        return userInterface.getUserByUsernameAndPassword(username,password);
    }
}
