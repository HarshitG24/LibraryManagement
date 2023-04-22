package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.model.Cart;
import com.example.distributedsystems.distributed.systems.model.CartContent;
import com.example.distributedsystems.distributed.systems.model.Employee;
import com.example.distributedsystems.distributed.systems.service.BookService;
import com.example.distributedsystems.distributed.systems.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/createCart")
    public ResponseEntity<Object> createCart(@RequestBody CartContent content) {
        List<Long> bks = new ArrayList<>();
        bks.add((long)content.getBid());
        Cart c = new Cart(content.getUser(), bks);
        cartService.createCart(c);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<Cart> getCartForUser(@RequestBody String user) {
        Cart cartBooks = cartService.getCartForUser(user);
        return new ResponseEntity<>(cartBooks, HttpStatus.OK);
    }

    @PostMapping("/addBook")
    public ResponseEntity<Object> addBookToCart(@RequestBody CartContent content) {
        cartService.updateCartForUser(content.getUser(), (long)content.getBid());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/deleteBook")
    public ResponseEntity<Object> deleteBookFromCart(@RequestBody CartContent content) {
        cartService.updateCartForUser(content.getUser(), (long)content.getBid());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/deleteAllCart")
    public ResponseEntity<Cart> deleteCartByUsername(@RequestBody String user) {
        Cart cartBooks = cartService.deleteCartForUser(user);
        return new ResponseEntity<>(cartBooks, HttpStatus.OK);
    }

//    @DeleteMapping("/deleteCart")
//    public ResponseEntity<List<Cart>> deleteCartByUsernameAndIsbn(@RequestBody Cart c) {
//        cartService.deleteCartByUserAndIsbn(c.getUsername(), c.getIsbn());
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
}
