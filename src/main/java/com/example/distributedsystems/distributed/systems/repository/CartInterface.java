package com.example.distributedsystems.distributed.systems.repository;

import com.example.distributedsystems.distributed.systems.model.cart.Cart;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartInterface extends CrudRepository<Cart, Integer> {
    Cart getCartByUsername(String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.username = :username")
    void deleteCartByUsername(@Param("username") String username);

    Cart findByUsername(String username);

//    Cart updateBooksByUsername(Cart c);

//    @Modifying
//    @Transactional
//    @Query("DELETE FROM Cart c WHERE c.isbn = :isbn AND c.username = :username")
//    void deleteCartByUsernameAndIsbn(String username, Long isbn);

}
