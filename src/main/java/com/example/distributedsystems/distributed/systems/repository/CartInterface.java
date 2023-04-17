package com.example.distributedsystems.distributed.systems.repository;

import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.model.Cart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartInterface extends CrudRepository<Cart, Integer> {
    List<Cart> getCartByUsername(String username);
    List<Cart> deleteCartByUsername(String username);
}
