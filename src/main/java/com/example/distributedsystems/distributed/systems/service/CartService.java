package com.example.distributedsystems.distributed.systems.service;

import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.model.Cart;
import com.example.distributedsystems.distributed.systems.model.CartContent;
import com.example.distributedsystems.distributed.systems.repository.CartInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    CartInterface cartInterface;

    public void createCart(Cart cart){
        cartInterface.save(cart);
    }

    public Cart getCartForUser(String user) {
        return cartInterface.getCartByUsername(user);
    }

    public Cart deleteCartForUser(String user) {
        return cartInterface.deleteCartByUsername(user);
    }

    public void updateCartForUser(Integer id, Long isbn){

        if(cartInterface.findById(id).isPresent()){
            Cart cart = cartInterface.findById(id).get();
            List<Long> books = cart.getAllBooks();
            books.add(isbn);
            cart.setAllBooks(books);
            cartInterface.save(cart);
        }
    }

//    public void deleteCartByUserAndIsbn(String user, Long isbn){
//        cartInterface.deleteBookInCartByUsername(user, isbn);
//    }
}
