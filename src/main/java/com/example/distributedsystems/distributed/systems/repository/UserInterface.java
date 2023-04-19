package com.example.distributedsystems.distributed.systems.repository;

import com.example.distributedsystems.distributed.systems.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInterface extends CrudRepository<User, Long> {
    User findByEmail(String email);
    User findByUsername(String username);
    User getUserByUsernameAndPassword(String username, String password);
    User getUserByUserId(Long userId);
}
