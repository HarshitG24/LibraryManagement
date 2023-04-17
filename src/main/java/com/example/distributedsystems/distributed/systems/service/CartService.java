package com.example.distributedsystems.distributed.systems.service;

import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.model.Cart;
import com.example.distributedsystems.distributed.systems.repository.CartInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    CartInterface cartInterface;

    public List<Cart> getCartForUser(String user) {
        return cartInterface.getCartByUsername(user);
    }

    public List<Cart> deleteCartForUser(String user) {
        return cartInterface.deleteCartByUsername(user);
    }
}
