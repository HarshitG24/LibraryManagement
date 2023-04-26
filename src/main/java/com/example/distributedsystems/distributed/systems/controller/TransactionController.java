package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.dsalgo.vectortimestamps.VectorTimestampService;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosController;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosScenario;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosTransaction;
import com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala.RicartAgrawalaHandler;
import com.example.distributedsystems.distributed.systems.model.transaction.Transaction;
import com.example.distributedsystems.distributed.systems.model.transaction.TransactionRequest;
import com.example.distributedsystems.distributed.systems.service.TransactionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/transaction")
public class TransactionController extends PaxosController {
  private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

  @Autowired
  private TransactionService transactionService;

  @Autowired
  public TransactionController(RicartAgrawalaHandler ricartAgrawalaHandler, VectorTimestampService vectorTimestampService) {
    super(ricartAgrawalaHandler, vectorTimestampService);
  }

  @GetMapping("")
  public ResponseEntity<List<Transaction>> getAllTransactions() {
    List<Transaction> transactions = transactionService.getAllTransactions();
    return new ResponseEntity<>(transactions, HttpStatus.OK);
  }

  @PostMapping("/createTransaction")
  public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionRequest transactionRequest) {
    logger.info("Create transaction request received. " + transactionRequest);
    // Used Paxos for consensus
    PaxosTransaction paxosTransaction = new PaxosTransaction(transactionRequest.getTransactionId(), transactionRequest.getUsername(), transactionRequest.getBookIds(), PaxosScenario.CHECKOUT);
    try {
      propose(paxosTransaction);
    } catch (Exception e) {
      logger.error("Exception: " + e.getMessage());
      return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }


  @PutMapping("/{transactionId}/book/{bookIsbn}")
  public ResponseEntity<Object> markBookReturned(@PathVariable Long transactionId, @PathVariable Long bookIsbn) {
    logger.info("Mark book return request received for Transaction(ID): " + transactionId + ", Book(ISBN): " + bookIsbn);
    List<Long> list = new ArrayList<>();
    list.add(bookIsbn);
    PaxosTransaction pt = new PaxosTransaction(transactionId, list, PaxosScenario.RETURN);
    // Used Paxos for consensus
    propose(pt);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/unreturned/{username}")
  public ResponseEntity<List<Long>> getAllUnreturnedBooksByUserId(@PathVariable String username) {
    logger.info("Get all unreturned books request received for User: " + username);
    List<Long> unreturnedBookIds = transactionService.getAllUnreturnedBooksByUsername(username);
    return new ResponseEntity<>(unreturnedBookIds, HttpStatus.OK);
  }

  @GetMapping("/returned/{username}")
  public ResponseEntity<List<Long>> getAllReturnedBooksByUserId(@PathVariable String username) {
    logger.info("Get all returned books request received for User: " + username);
    List<Long> returnedBookIds = transactionService.getAllReturnedBooksByUsername(username);
    return new ResponseEntity<>(returnedBookIds, HttpStatus.OK);
  }

  @GetMapping("/userId/{username}")
  public ResponseEntity<List<Transaction>> getAllTransactionByUserId(@PathVariable String username) {
    logger.info("Get all transactions request received for User: " + username);
    List<Transaction> transactions = transactionService.getAllTransactionsByUsername(username);
    return new ResponseEntity<>(transactions, HttpStatus.OK);
  }

  @GetMapping("/{transactionId}")
  public ResponseEntity<Transaction> getTransactionByTransactionId(@PathVariable Long transactionId) {
    logger.info("Get transaction request received for Transaction(ID): " + transactionId);
    Transaction transaction = transactionService.getTransactionByTransactionId(transactionId);
    return new ResponseEntity<>(transaction, HttpStatus.OK);
  }
}
