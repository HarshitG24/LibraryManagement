package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosController;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosScenario;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosTransaction;
import com.example.distributedsystems.distributed.systems.model.transaction.Transaction;
import com.example.distributedsystems.distributed.systems.model.transaction.TransactionRequest;
import com.example.distributedsystems.distributed.systems.service.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/transaction")
public class TransactionController extends PaxosController {

  @Autowired
  private TransactionService transactionService;

  @GetMapping("")
  public ResponseEntity<List<Transaction>> getAllTransactions() {

    List<Transaction> transactions = transactionService.getAllTransactions();
    System.out.println("Transactions:" + transactions);
    return new ResponseEntity<>(transactions, HttpStatus.OK);
  }

  @PostMapping("/createTransaction")
  public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionRequest transactionRequest) {
//    Transaction transaction = new Transaction(transactionRequest.getTransactionId(), transactionRequest.getUsername(), transactionRequest.getBookIds());
//    Transaction savedTransaction = transactionService.createTransaction(transaction);

    // 1. call paxos here and save the transaction in learners phase
    PaxosTransaction pt = new PaxosTransaction(transactionRequest.getTransactionId(), transactionRequest.getUsername(), transactionRequest.getBookIds(), PaxosScenario.CHECKOUT);
//    return ResponseEntity.ok(savedTransaction);
    propose(pt);
    return new ResponseEntity<>(HttpStatus.OK);
  }


  @PutMapping("/{transactionId}/book/{bookId}")
  public ResponseEntity<Object> markBookReturned(@PathVariable Long transactionId, @PathVariable Long bookId) {
    // 2. call paxos here and save the transaction in learners phase
    // tid, isbn, usename
    transactionService.updateBookReturnedByTransactionId(transactionId, bookId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/unreturned/{username}")
  public ResponseEntity<List<Long>> getAllUnreturnedBooksByUserId(@PathVariable String username) {
    List<Long> unreturnedBookIds = transactionService.getAllUnreturnedBooksByUsername(username);
    return new ResponseEntity<>(unreturnedBookIds, HttpStatus.OK);
  }

  @GetMapping("/returned/{username}")
  public ResponseEntity<List<Long>> getAllReturnedBooksByUserId(@PathVariable String username) {
    List<Long> returnedBookIds = transactionService.getAllReturnedBooksByUsername(username);
    return new ResponseEntity<>(returnedBookIds, HttpStatus.OK);
  }

  @GetMapping("/userId/{username}")
  public ResponseEntity<List<Transaction>> getAllTransactionByUserId(@PathVariable String username) {
    List<Transaction> transactions = transactionService.getAllTransactionsByUsername(username);
    return new ResponseEntity<>(transactions, HttpStatus.OK);
  }

  @GetMapping("/{transactionId}")
  public ResponseEntity<Transaction> getTransactionByTransactionId(@PathVariable Long transactionId) {
    Transaction transaction = transactionService.getTransactionByTransactionId(transactionId);
    return new ResponseEntity<>(transaction, HttpStatus.OK);
  }
}
