package com.example.distributedsystems.distributed.systems.repository;

import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.model.Cart;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartInterface extends CrudRepository<Cart, Integer> {
    List<Cart> getCartByUsername(String username);
    List<Cart> deleteCartByUsername(String username);
    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.isbn = :isbn AND c.username = :username")
    void deleteCartByUsernameAndIsbn(String username, Long isbn);
}
