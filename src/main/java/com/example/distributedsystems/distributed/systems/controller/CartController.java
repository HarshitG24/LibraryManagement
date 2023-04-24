package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosController;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosScenario;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosTransaction;
import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.model.cart.Cart;
import com.example.distributedsystems.distributed.systems.model.cart.CartBook;
import com.example.distributedsystems.distributed.systems.model.cart.CartBookId;
import com.example.distributedsystems.distributed.systems.model.cart.CartBooksResponse;
import com.example.distributedsystems.distributed.systems.model.cart.CartDTO;
import com.example.distributedsystems.distributed.systems.model.cart.CartRequest;
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
public class CartController extends PaxosController {

    //TODO: Cart Controller
    @Autowired
    private CartService cartService;

    @Autowired
    private BookService bookService;

    @GetMapping("/all")
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        System.out.println("All carts");
        List<CartDTO> carts = cartService.getAllCarts();
        System.out.println("Get all carts: " + carts);
        return new ResponseEntity<>(carts, HttpStatus.OK);
    }

    @GetMapping("/cartBooks/all")
    public ResponseEntity<List<CartBookId>> getAllCartBooks() {
        List<CartBookId> cartBooks = cartService.getAllCartBooks();
        return new ResponseEntity<>(cartBooks, HttpStatus.OK);
    }

    @PostMapping("/createCart")
    public ResponseEntity<Object> createCart(@RequestBody CartRequest content) {
        Book book = bookService.getBookByIsbn(content.getIsbn());
        if (book == null) {
            // handle case where book is not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Cart cart = new Cart(content.getUsername());
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
        // paxos here

        List<Long> list = new ArrayList<>();
        list.add(content.getIsbn());
        PaxosTransaction pt = new PaxosTransaction(content.getUsername(), list, PaxosScenario.LOAN);
//        System.out.println("add book: " + content);
//        cartService.updateCartForUser(content.getUsername(), content.getIsbn());
        propose(pt);
        return new ResponseEntity<>(content.getIsbn(), HttpStatus.OK);
    }

    @DeleteMapping("/{username}/book/{isbn}")
    public ResponseEntity<Object> deleteBookFromCartForUser(@PathVariable String username, @PathVariable Long isbn) {
        // paxos here
//        cartService.deleteBookFromCartForUser(username, isbn);

        List<Long> list = new ArrayList<>();
        list.add(isbn);
        PaxosTransaction pt = new PaxosTransaction(username, list, PaxosScenario.LOAN);
        propose(pt);
        return new ResponseEntity<>(isbn, HttpStatus.OK);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Object> deleteCartByUsername(@PathVariable String username) {
        // paxos here
        cartService.deleteCartByUsername(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
