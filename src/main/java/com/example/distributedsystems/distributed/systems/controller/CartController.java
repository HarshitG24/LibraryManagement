package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.model.cart.Cart;
import com.example.distributedsystems.distributed.systems.model.cart.CartBook;
import com.example.distributedsystems.distributed.systems.model.cart.CartBooksResponse;
import com.example.distributedsystems.distributed.systems.model.cart.CartRequest;
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

    @Autowired
    private BookService bookService;

    @PostMapping("/createCart")
    public ResponseEntity<Object> createCart(@RequestBody CartRequest content) {
        Book book = bookService.getBookByIsbn(content.getIsbn());
        if (book == null) {
            // handle case where book is not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Cart cart = new Cart(content.getUser());
        CartBook cartBook = new CartBook(cart, book);
        cart.getCartBooks().add(cartBook);
        cartService.createCart(cart);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<CartBooksResponse> getBooksFromCart(@RequestParam String username) {
        List<Long> books = cartService.getAllBooksInCartByUsername(username);
        CartBooksResponse response = new CartBooksResponse(username, books);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/addBook")
    public ResponseEntity<Object> addBookToCart(@RequestBody CartRequest content) {
        System.out.println(content);

        cartService.updateCartForUser(content.getUser(), content.getIsbn());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{username}/book/{isbn}")
    public ResponseEntity<Object> deleteBookFromCartForUser(@PathVariable String username, @PathVariable Long isbn) {
        cartService.deleteBookFromCartForUser(username, isbn);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Object> deleteCartByUsername(@PathVariable String username) {
        cartService.deleteCartByUsername(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
