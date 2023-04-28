package com.example.distributedsystems.distributed.systems.repository;

import com.example.distributedsystems.distributed.systems.model.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for managing User entities in the system.
 */
@Repository
public interface UserInterface extends CrudRepository<User, Long> {

    /**
     * Retrieves a user by their email address.
     *
     * @param email The email address of the user.
     * @return The user with the specified email address.
     */
    User findByEmail(String email);

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user.
     * @return The user with the specified username.
     */
    User findByUsername(String username);

    /**
     * Retrieves a user by their username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The user with the specified username and password.
     */
    User getUserByUsernameAndPassword(String username, String password);

    /**
     * Retrieves a user by their userId.
     *
     * @param userId The userId of the user.
     * @return The user with the specified userId.
     */
    User getUserByUserId(Long userId);
}
