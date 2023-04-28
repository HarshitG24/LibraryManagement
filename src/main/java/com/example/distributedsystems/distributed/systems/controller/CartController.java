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

/**
 * The class to expose functions of cart to frontend
 */
@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/cart")
public class CartController extends PaxosController {

    // Making an instance of logger class to log the activities in program flow
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    // to use methods in cart repository
    @Autowired
    private CartService cartService;

    // to use methods in book repository
    @Autowired
    private BookService bookService;

    /**
     * Constructor for the class
     * @param ricartAgrawalaHandler - Object of RicartAgrawalaHandler class used with vector timestamps and paxos
     * @param vectorTimestampService - Object of VectorTimestampService class used with  paxos and ricart-agrawala
     */
    @Autowired
    public CartController(RicartAgrawalaHandler ricartAgrawalaHandler, VectorTimestampService vectorTimestampService) {
        super(ricartAgrawalaHandler, vectorTimestampService);
    }


    /**
     * Method to get all the carts from the database
     * @return - ResponseEntity object with status code and List of all carts
     */
    @GetMapping("/all")
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        logger.info("Get all carts request received.");
        List<CartDTO> carts = cartService.getAllCarts();
        return new ResponseEntity<>(carts, HttpStatus.OK);
    }

    /**
     * Method to get all the books in cart
     * @return - ResponseEntity object with status code and List of all books in cart
     */
    @GetMapping("/cartBooks/all")
    public ResponseEntity<List<CartBookId>> getAllCartBooks() {
        logger.info("Get all cart books request received.");
        List<CartBookId> cartBooks = cartService.getAllCartBooks();
        return new ResponseEntity<>(cartBooks, HttpStatus.OK);
    }

    /**
     * Method to create carts for a user, if doesn't exist
     * @return - ResponseEntity object with status code
     */
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

    /**
     * Method to get books from cart for a given user
     * @param username - the user for whom we are finding books in cart
     * @return - ResponseEntity object with status code and the CartBooksResponse object containing username and books in cart
     */
    @GetMapping("")
    public ResponseEntity<CartBooksResponse> getBooksFromCart(@RequestParam String username) {
        logger.info("Get books from cart request received for User: " + username);
        List<Long> books = cartService.getAllBooksInCartByUsername(username);
        CartBooksResponse response = new CartBooksResponse(username, books);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Adding book to the cart for the given user
     * @param content - Object of CartRequest containing the username and isbn which needs to be added
     * @return - ResponseEntity object with status code and the Response object containing message and boolean value if paxos was successful
     */
    @PostMapping("/addBook")
    public ResponseEntity<Response> addBookToCart(@RequestBody CartRequest content) {
        logger.info("Add book to cart request received. CartRequest: " + content);
        List<Long> list = new ArrayList<>();
        list.add(content.getIsbn());
        // Used Paxos for consensus
        PaxosTransaction paxosTransaction = new PaxosTransaction(content.getUsername(), list, PaxosScenario.LOAN);
        ResponseEntity<Response> addBookResponse;
        try {
            addBookResponse = propose(paxosTransaction); // starting paxos
            Response responseStatus = addBookResponse.getBody();
            assert responseStatus != null;
            if (responseStatus.isSuccess()) {
                Response responseObject = new Response(responseStatus.isSuccess(), responseStatus.getMessage(), content.getIsbn(), null);
                addBookResponse = new ResponseEntity<>(responseObject, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
        return addBookResponse;
    }

    /**
     * Delete the book from cart
     * @param username - The user for which we need to delete the book
     * @param isbn - the book to detele from cart
     * @return - ResponseEntity object with status code and the Response object containing message and boolean value if paxos was successful
     */
    @DeleteMapping("/{username}/book/{isbn}")
    public ResponseEntity<Response> deleteBookFromCartForUser(@PathVariable String username, @PathVariable Long isbn) {
        logger.info("Delete book from cart request received for User: " + username + ", ISBN: " + isbn);
        List<Long> list = new ArrayList<>();
        list.add(isbn);
        PaxosTransaction paxosTransaction = new PaxosTransaction(username, list, PaxosScenario.DELETE_BOOK);
        // Used Paxos for consensus
        ResponseEntity<Response> deleteBookResponse;
        try {
            deleteBookResponse = propose(paxosTransaction); // starting paxos
            Response responseStatus = deleteBookResponse.getBody();
            assert responseStatus != null;
            if (responseStatus.isSuccess()) {
                Response responseObject = new Response(responseStatus.isSuccess(), responseStatus.getMessage(), isbn, null);
                deleteBookResponse = new ResponseEntity<>(responseObject, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
        return deleteBookResponse;
    }

    /**
     * Method to delete the cart completely and all the books from database
     * @param username - The user for which we need to delete the cart
     * @return - ResponseEntity object with status code and the Response object containing message and boolean value if paxos was successful
     */
    @DeleteMapping("/{username}")
    public ResponseEntity<Response> deleteCartByUsername(@PathVariable String username) {
        logger.info("Delete cart request received for User: " + username);
        // Used Paxos for consensus
        PaxosTransaction paxosTransaction = new PaxosTransaction(username, PaxosScenario.DELETE_CART);
        ResponseEntity<Response> deleteCartResponse;
        try {
            deleteCartResponse = propose(paxosTransaction); // starting paxos
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
        return deleteCartResponse;
    }
}
