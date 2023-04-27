package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.dsalgo.vectortimestamps.VectorTimestampService;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosController;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosScenario;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosTransaction;
import com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala.RicartAgrawalaHandler;
import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.model.Response;
import com.example.distributedsystems.distributed.systems.model.cart.Cart;
import com.example.distributedsystems.distributed.systems.model.cart.CartBook;
import com.example.distributedsystems.distributed.systems.model.cart.CartBookId;
import com.example.distributedsystems.distributed.systems.model.cart.CartBooksResponse;
import com.example.distributedsystems.distributed.systems.model.cart.CartDTO;
import com.example.distributedsystems.distributed.systems.model.cart.CartRequest;
import com.example.distributedsystems.distributed.systems.service.BookService;
import com.example.distributedsystems.distributed.systems.service.CartService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private BookService bookService;

    @Autowired
    public CartController(RicartAgrawalaHandler ricartAgrawalaHandler, VectorTimestampService vectorTimestampService) {
        super(ricartAgrawalaHandler, vectorTimestampService);
    }


    @GetMapping("/all")
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        logger.info("Get all carts request received.");
        List<CartDTO> carts = cartService.getAllCarts();
        return new ResponseEntity<>(carts, HttpStatus.OK);
    }

    @GetMapping("/cartBooks/all")
    public ResponseEntity<List<CartBookId>> getAllCartBooks() {
        logger.info("Get all cart books request received.");
        List<CartBookId> cartBooks = cartService.getAllCartBooks();
        return new ResponseEntity<>(cartBooks, HttpStatus.OK);
    }

    @PostMapping("/createCart")
    public ResponseEntity<Object> createCart(@RequestBody CartRequest content) {
        logger.info("Create cart request received. CartRequest: " + content);
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
        logger.info("Get books from cart request received for User: " + username);
        List<Long> books = cartService.getAllBooksInCartByUsername(username);
        CartBooksResponse response = new CartBooksResponse(username, books);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/addBook")
    public ResponseEntity<Response> addBookToCart(@RequestBody CartRequest content) {
        logger.info("Add book to cart request received. CartRequest: " + content);
        List<Long> list = new ArrayList<>();
        list.add(content.getIsbn());
        // Used Paxos for consensus
        PaxosTransaction pt = new PaxosTransaction(content.getUsername(), list, PaxosScenario.LOAN);
        ResponseEntity<Response> ans;
        try {
            ans = propose(pt);
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

        return ans;
//        return new ResponseEntity<>(content.getIsbn(), HttpStatus.OK);
    }

    @DeleteMapping("/{username}/book/{isbn}")
    public ResponseEntity<Response> deleteBookFromCartForUser(@PathVariable String username, @PathVariable Long isbn) {
        logger.info("Delete book from cart request received for User: " + username + ", ISBN: " + isbn);
        List<Long> list = new ArrayList<>();
        list.add(isbn);
        PaxosTransaction pt = new PaxosTransaction(username, list, PaxosScenario.DELETE_BOOK);
        // Used Paxos for consensus
//        propose(pt);
//        try {
//            propose(pt);
//        } catch (Exception e) {
//            logger.error("Exception: " + e.getMessage());
//            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
//        }
//        return new ResponseEntity<>(isbn, HttpStatus.OK);
        ResponseEntity<Response> ans;
        try {
            ans = propose(pt);
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

        return ans;
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Response> deleteCartByUsername(@PathVariable String username) {
        logger.info("Delete cart request received for User: " + username);
        // Used Paxos for consensus
        PaxosTransaction pt = new PaxosTransaction(username, PaxosScenario.DELETE_CART);
//        propose(pt);
//        try {
//            propose(pt);
//        } catch (Exception e) {
//            logger.error("Exception: " + e.getMessage());
//            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
//        }
//        return new ResponseEntity<>(HttpStatus.OK);
        ResponseEntity<Response> ans;
        try {
            ans = propose(pt);
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

        return ans;
    }
}
