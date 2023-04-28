package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.coordinator.RestService;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosTransaction;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.Promise;
import com.example.distributedsystems.distributed.systems.model.transaction.Transaction;
import com.example.distributedsystems.distributed.systems.repository.PxRepository;
import com.example.distributedsystems.distributed.systems.service.BookService;
import com.example.distributedsystems.distributed.systems.service.CartService;
import com.example.distributedsystems.distributed.systems.service.TransactionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The class for running paxos algorithm
 */
@Controller
@RequestMapping("/paxos")
public class PXController {

    // Making an instance of logger class to log the activities in program flow
    private static final Logger logger = LoggerFactory.getLogger(PXController.class);

    // to determine the current port
    @Value("${server.port}")
    private int serverPort;

    // to generate request body and make api calls
    @Autowired
    RestService restService;

    //  to call methods in the repository to modify
    @Autowired
    PxRepository repository;

    // to use methods in transaction repository
    @Autowired
    private TransactionService transactionService;

    // to use methods in book repository
    @Autowired
    private BookService bookService;

    // to use methods in cart repository
    @Autowired
    private CartService cartService;

    // to determine which is the max id seen, needed for paxos
    private int maxIdSeen = 0;

    // to determine which is the max timestamp seen, needed for paxos
    private long maxTimeStampSeen;

    /**
     * Prepare phase of paxos algorithm
     * @param transaction - instance of PaxosTransaction class which contains the details such as transactionid, username  needed for paxos
     * @return - ResponseEntity object with status code and promise object, if the node promises to accept the proposal and the max proposal value seen
     */
    @PostMapping("/prepare")
    public ResponseEntity<Object> prepare(@RequestBody PaxosTransaction transaction) {

        // Random failure of node with probability of 10%
        if (Math.random() <= 0.1) {
            logger.error("(Intention Fail!) Node failed for PREPARE at port: " + serverPort);
            return new ResponseEntity<>(new Promise(false, Long.MIN_VALUE), HttpStatus.OK);
        }

        // to check if the proposal id is greater than the id seen
        if (transaction.getProposalId() > maxTimeStampSeen) {
            maxTimeStampSeen = transaction.getProposalId();
            logger.info("Promised proposal: " + transaction.getProposalId() + " at port: " + serverPort);
            return new ResponseEntity<>(new Promise(true, transaction.getProposalId()), HttpStatus.OK);
        } else {
            logger.info("Promise Denied for proposal: " + transaction.getProposalId() + " at port: " + serverPort);
            return new ResponseEntity<>(new Promise(false, Long.MIN_VALUE), HttpStatus.OK);
        }
    }

    /**
     * Prepare phase of paxos algorithm
     * @param transaction - instance of PaxosTransaction class which contains the details such as transactionid, username  needed for paxos
     * @return - ResponseEntity object with status code and max transaction id seen by node
     */
    @PostMapping("/accept")
    public ResponseEntity<Object> accept(@RequestBody PaxosTransaction transaction) {
        // Random failure of node with probability of 10%
        if (Math.random() <= 0.1) {
            logger.error("(Intention Fail!) Node failed for ACCEPT at port: " +serverPort);
            return new ResponseEntity<>(Long.MIN_VALUE, HttpStatus.OK);
        }
        // to check if the proposal id is greater than the id seen
        if (transaction.getProposalId() >= maxTimeStampSeen) {
            logger.info("Accepted proposal: " + transaction.getProposalId() + " at port: " + serverPort);
            return new ResponseEntity<>(transaction.getProposalId(), HttpStatus.OK);
        } else {
            logger.info("Rejected proposal: " + transaction.getProposalId() + " at port: " + serverPort);
            return new ResponseEntity<>(Long.MIN_VALUE, HttpStatus.OK);
        }
    }

    /**
     * Learning phase of paxos, if the consensus was acheieved and execute the method depending upon the scenario
     * @param t - instance of PaxosTransaction class which contains the details such as transactionid, username  needed for paxos
     * @return -  ResponseEntity object with status code and true/false if update was successful
     */

    @PostMapping("/learn")
    public ResponseEntity<Object> learn(@RequestBody PaxosTransaction t) {
        switch (t.getScenario()){
            case CHECKOUT:  //Loaning book from cart
                checkout(t);
                break;

            case RETURN:  //Returning a book
                returnBook(t);
                break;

            case LOAN:  //Add book to cart to loan
                loan(t);
                break;

            case DELETE_BOOK:  //Delete book from cart
                deleteBook(t);
                break;

            case DELETE_CART: //Delete cart
                deleteCart(t);
                break;

            default:
                return new ResponseEntity<>(HttpStatus.BAD_GATEWAY); //
        }
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    /**
     * When user checkouts on cart page, so we make the necessary updates in database
     * @param pt - instance of PaxosTransaction class which contains the details such as transactionid, username  needed for learning phase
     */
    public void checkout(PaxosTransaction pt){
        Transaction transaction = new Transaction(pt.getTransactionId(), pt.getUsername(), pt.getAllBooks());
        Transaction savedTransaction = transactionService.createTransaction(transaction);

        transactionService.createTransaction(savedTransaction);
    }

    /**
     * When user returns the books back from the returns page
     * @param pt - instance of PaxosTransaction class which contains the details such as transactionid, username  needed for learning phase
     */
    public void returnBook(PaxosTransaction pt){
        transactionService.updateBookReturnedByTransactionId(pt.getTransactionId(), pt.getAllBooks().get(0));
    }

    /**
     * When user loans the books back from the loans page
     * @param pt - instance of PaxosTransaction class which contains the details such as transactionid, username  needed for learning phase
     */
    public void loan(PaxosTransaction pt){
        cartService.updateCartForUser(pt.getUsername(), pt.getAllBooks().get(0));
    }

    /**
     * When user deletes the books from the cart page
     * @param pt - instance of PaxosTransaction class which contains the details such as transactionid, username, bookid  needed for learning phase
     */
    public void deleteBook(PaxosTransaction pt){
        cartService.deleteBookFromCartForUser(pt.getUsername(), pt.getAllBooks().get(0));
    }

    /**
     * When user wants to delete all the books from the cart page
     * @param pt - instance of PaxosTransaction class which contains the details such as transactionid, username  needed for learning phase
     */
    public void deleteCart(PaxosTransaction pt){
        cartService.deleteCartByUsername(pt.getUsername());
    }
}