package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.coordinator.RestService;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosTransaction;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.Promise;
import com.example.distributedsystems.distributed.systems.model.transaction.Transaction;
import com.example.distributedsystems.distributed.systems.repository.PxRepository;
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

@Controller
@RequestMapping("/paxos")
public class PXController {
    private static final Logger logger = LoggerFactory.getLogger(PXController.class);

    @Value("${server.port}")
    private int serverPort;

    @Autowired
    RestService restService;

    @Autowired
    PxRepository repository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CartService cartService;

    private int maxIdSeen = 0;

    private long maxTimeStampSeen;

    @PostMapping("/prepare")
    public ResponseEntity<Object> prepare(@RequestBody PaxosTransaction transaction) {

        if (Math.random() <= 0.1) {
            logger.error("(Intention Fail!) Node failed for PREPARE at port: " + serverPort);
            return new ResponseEntity<>(new Promise(false, Long.MIN_VALUE), HttpStatus.OK);
        }

//        System.out.println("max id seen is: " + maxTimeStampSeen + ", pid is: " + transaction.getProposalId());

//        Paxos paxos = repository.getById(1);
        if (transaction.getProposalId() > maxTimeStampSeen) {
//            paxos.setMinId(transaction.getTransactionId());
//            repository.save(paxos);
            maxTimeStampSeen = transaction.getProposalId();
            logger.info("Promised proposal: " + transaction.getProposalId() + " at port: " + serverPort);
            return new ResponseEntity<>(new Promise(true, transaction.getProposalId()), HttpStatus.OK);
        } else {
            logger.info("Promise Denied for proposal: " + transaction.getProposalId() + " at port: " + serverPort);
            return new ResponseEntity<>(new Promise(false, Long.MIN_VALUE), HttpStatus.OK);
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<Object> accept(@RequestBody PaxosTransaction transaction) {
        if (Math.random() <= 0.1) {
            logger.error("(Intention Fail!) Node failed for ACCEPT at port: " +serverPort);
            return new ResponseEntity<>(Long.MIN_VALUE, HttpStatus.OK);
        }
        if (transaction.getProposalId() >= maxIdSeen) {
            logger.info("Accepted proposal: " + transaction.getProposalId() + " at port: " + serverPort);
            return new ResponseEntity<>(transaction.getProposalId(), HttpStatus.OK);
        } else {
            logger.info("Rejected proposal: " + transaction.getProposalId() + " at port: " + serverPort);
            return new ResponseEntity<>(Long.MIN_VALUE, HttpStatus.OK);
        }
    }

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
                return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    public void checkout(PaxosTransaction pt){
        Transaction transaction = new Transaction(pt.getTransactionId(), pt.getUserId(), pt.getAllBooks());
        Transaction savedTransaction = transactionService.createTransaction(transaction);
        transactionService.createTransaction(savedTransaction);
    }

    public void returnBook(PaxosTransaction pt){
        transactionService.updateBookReturnedByTransactionId(pt.getTransactionId(), pt.getAllBooks().get(0));
    }

    public void loan(PaxosTransaction pt){
        cartService.updateCartForUser(pt.getUserId(), pt.getAllBooks().get(0));
    }

    public void deleteBook(PaxosTransaction pt){
        cartService.deleteBookFromCartForUser(pt.getUserId(), pt.getAllBooks().get(0));
    }

    public void deleteCart(PaxosTransaction pt){
        cartService.deleteCartByUsername(pt.getUserId());
    }
}