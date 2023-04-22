package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.model.Transaction;
import com.example.distributedsystems.distributed.systems.model.TransactionRequest;
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
public class TransactionController {

  @Autowired
  private TransactionService transactionService;

  @GetMapping("/")
  public ResponseEntity<List<Transaction>> getAllTransactions() {

    List<Transaction> transactions = transactionService.getAllTransactions();
    System.out.println("Transactions:" + transactions);
    return new ResponseEntity<>(transactions, HttpStatus.OK);
  }

  @PostMapping("/createTransaction")
  public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionRequest transactionRequest) {
    Transaction transaction = new Transaction(transactionRequest.getUserId(), transactionRequest.getBookIds());
    Transaction savedTransaction = transactionService.createTransaction(transaction);
    return ResponseEntity.ok(savedTransaction);
  }


  @PutMapping("/{transactionId}/book/{bookId}")
  public ResponseEntity<Object> markBookReturned(@PathVariable Long transactionId, @PathVariable Long bookId) {
    transactionService.updateBookReturnedByTransactionId(transactionId, bookId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/unreturned/{userId}")
  public ResponseEntity<List<Long>> getAllUnreturnedBooksByUserId(@PathVariable Long userId) {
    List<Long> unreturnedBookIds = transactionService.getAllUnreturnedBooksByUserId(userId);
    return new ResponseEntity<>(unreturnedBookIds, HttpStatus.OK);
  }

  @GetMapping("/returned/{userId}")
  public ResponseEntity<List<Long>> getAllReturnedBooksByUserId(@PathVariable Long userId) {
    List<Long> returnedBookIds = transactionService.getAllReturnedBooksByUserId(userId);
    return new ResponseEntity<>(returnedBookIds, HttpStatus.OK);
  }

  @GetMapping("/userId/{userId}")
  public ResponseEntity<List<Transaction>> getAllTransactionByUserId(@PathVariable Long userId) {
    List<Transaction> transactions = transactionService.getAllTransactionsByUserId(userId);
    return new ResponseEntity<>(transactions, HttpStatus.OK);
  }

  @GetMapping("/{transactionId}")
  public ResponseEntity<Transaction> getTransactionByTransactionId(@PathVariable Long transactionId) {
    Transaction transaction = transactionService.getTransactionByTransactionId(transactionId);
    return new ResponseEntity<>(transaction, HttpStatus.OK);
  }
}
