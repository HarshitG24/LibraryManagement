package com.example.distributedsystems.distributed.systems.repository;

import com.example.distributedsystems.distributed.systems.model.cart.Cart;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository class for managing Cart entities and their relationships with Book entities.
 */
@Repository
public interface CartInterface extends CrudRepository<Cart, Integer> {

    /**
     * Retrieves the cart with the specified username.
     *
     * @param username The username of the cart to be retrieved.
     * @return The cart with the specified username.
     */
    Cart getCartByUsername(String username);

    /**
     * Deletes the cart with the specified username.
     *
     * @param username The username of the cart to be deleted.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.username = :username")
    void deleteCartByUsername(@Param("username") String username);

    /**
     * Finds the cart with the specified username.
     *
     * @param username The username of the cart to be found.
     * @return The cart with the specified username.
     */
    Cart findByUsername(String username);
}
