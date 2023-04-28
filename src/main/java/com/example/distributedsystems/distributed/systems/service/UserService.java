package com.example.distributedsystems.distributed.systems.service;

import com.example.distributedsystems.distributed.systems.dsalgo.twopc.TwoPCPromise;
import com.example.distributedsystems.distributed.systems.model.Response;
import com.example.distributedsystems.distributed.systems.model.user.User;
import com.example.distributedsystems.distributed.systems.repository.UserInterface;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * all the services for register/login users
 */
@Service
public class UserService {
    @Autowired
    private UserInterface userInterface;

    /**
     *
     * @return all the user registered in the sytsem
     */
    public List<User> getAllUsers() {
        return (List<User>) userInterface.findAll();
    }

    /**
     *
     * @param userId
     * @return returns the user detail for a given user id
     */
    public User getUserbyUserId(Long userId) {
        return  userInterface.getUserByUserId(userId);
    }

    /**
     *
     * @param user object that stores all the details of user when registered
     * @return create the user and the add the user record in the database
     */
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

    /**
     *
     * @param username
     * @param password
     * @return return the user details using username and password
     */
    public User getUserByUsernameAndPassword(String username, String password) {
        return userInterface.getUserByUsernameAndPassword(username,password);
    }
}
