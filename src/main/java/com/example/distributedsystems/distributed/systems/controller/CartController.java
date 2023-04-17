package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.model.Cart;
import com.example.distributedsystems.distributed.systems.model.Employee;
import com.example.distributedsystems.distributed.systems.service.BookService;
import com.example.distributedsystems.distributed.systems.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/")
    public ResponseEntity<List<Cart>> getCartForUser(@RequestBody String user) {
        List<Cart> cartBooks = cartService.getCartForUser(user);
        return new ResponseEntity<>(cartBooks, HttpStatus.OK);
    }

    @GetMapping("/deleteCart")
    public ResponseEntity<List<Cart>> deleteCartByUsername(@RequestBody String user) {
        List<Cart> cartBooks = cartService.deleteCartForUser(user);
        return new ResponseEntity<>(cartBooks, HttpStatus.OK);
    }
}
