package com.example.distributedsystems.distributed.systems.service;

import com.example.distributedsystems.distributed.systems.model.User;
import com.example.distributedsystems.distributed.systems.repository.UserInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserService {
    @Autowired
    private UserInterface userInterface;

    public User getUserbyUserId(Long user_id) {
        return  userInterface.getUserByUserId(user_id);
    }

    public void createUser(User e) {
        userInterface.save(e);
    }

    public User getUserByEmailAndPassword(String email, String password) {
        return userInterface.getUserByEmailAndPassword(email,password);
    }
}
