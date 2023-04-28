package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.dsalgo.vectortimestamps.VectorTimestampService;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosController;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosScenario;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosTransaction;
import com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala.RicartAgrawalaHandler;
import com.example.distributedsystems.distributed.systems.model.Response;
import com.example.distributedsystems.distributed.systems.model.transaction.Transaction;
import com.example.distributedsystems.distributed.systems.model.transaction.TransactionRequest;
import com.example.distributedsystems.distributed.systems.model.transaction.TransactionResponse;
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

/**
 * The class for transaction which is used when we loan, return book
 */
@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/transaction")
public class TransactionController extends PaxosController {

  // Making an instance of logger class to log the activities in program flow
  private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

  @Autowired
  private TransactionService transactionService; // we autowire the transactionservices to use the methods in repository for transaction class

  /**
   * Constructor for the class
   * @param ricartAgrawalaHandler - Object of RicartAgrawalaHandler class used with vector timestamps and paxos
   * @param vectorTimestampService - Object of VectorTimestampService class used with  paxos and ricart-agrawala
   */
  @Autowired
  public TransactionController(RicartAgrawalaHandler ricartAgrawalaHandler, VectorTimestampService vectorTimestampService) {
    super(ricartAgrawalaHandler, vectorTimestampService);
  }

  /**
   * Method to get all the transactions from the database
   * @return - ResponseEntity object with status code and list of all the transactions stored in the database
   */
  @GetMapping("")
  public ResponseEntity<List<Transaction>> getAllTransactions() {
    List<Transaction> transactions = transactionService.getAllTransactions();
    return new ResponseEntity<>(transactions, HttpStatus.OK);
  }

  /**
   * Method to create a transaction when we checkout on cart page
   * @param transactionRequest - The body of the request needed to make an transactioon
   * @return - ResponseEntity object with status code and object of response class containing the message and if paxos was successful
   */

  @PostMapping("/createTransaction")
  public ResponseEntity<Response> createTransaction(@RequestBody TransactionRequest transactionRequest) {
    logger.info("Create transaction request received. " + transactionRequest);
    // Used Paxos for consensus
    PaxosTransaction paxosTransaction = new PaxosTransaction(transactionRequest.getTransactionId(), transactionRequest.getUsername(), transactionRequest.getBookIsbns(), PaxosScenario.CHECKOUT);
    ResponseEntity<Response> createTransactionResponse;
    try {
      createTransactionResponse = propose(paxosTransaction); // starting paxos
    } catch (Exception e) {
      logger.error("Exception: " + e.getMessage());
      return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    return createTransactionResponse;
  }

  /**
   * Method to mark book as returned, seeting the returned column as true, which is false otherwise
   * @param transactionId - The transaction id to look for, the one associated with loaning book
   * @param bookIsbn - The book user wants to return
   * @return - ResponseEntity object with status code and object of response class containing the message and if paxos was successful
   */

  @PutMapping("/{transactionId}/book/{bookIsbn}")
  public ResponseEntity<Response> markBookReturned(@PathVariable Long transactionId, @PathVariable Long bookIsbn) {
    logger.info("Mark book return request received for Transaction(ID): " + transactionId + ", Book(ISBN): " + bookIsbn);
    List<Long> list = new ArrayList<>();
    list.add(bookIsbn);
    PaxosTransaction pt = new PaxosTransaction(transactionId, list, PaxosScenario.RETURN);
    // Used Paxos for consensus
    ResponseEntity<Response> markBookReturnedResponse;
    try {
      markBookReturnedResponse = propose(pt); // starting paxos
      Response responseStatus = markBookReturnedResponse.getBody();
      assert responseStatus != null;
      if (responseStatus.isSuccess()) {
        Response responseObject = new Response(responseStatus.isSuccess(), responseStatus.getMessage(), bookIsbn, transactionId);
        markBookReturnedResponse = new ResponseEntity<>(responseObject, HttpStatus.OK);
      }
    } catch (Exception e) {
      logger.error("Exception: " + e.getMessage());
      return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    return markBookReturnedResponse;
  }

  /**
   * Method to get list of all books not returned by user
   * @param username - The user for whom we want to search the unreturned books
   * @return - ResponseEntity object with status code and object of transaction response with details of transaction
   */
  @GetMapping("/unreturned/{username}")
  public ResponseEntity<List<TransactionResponse>> getAllUnreturnedBooksByUserId(@PathVariable String username) {
    logger.info("Get all unreturned books request received for User: " + username);
    List<TransactionResponse> unreturnedBookIds = transactionService.getAllUnreturnedBooksByUsername(username);
    return new ResponseEntity<>(unreturnedBookIds, HttpStatus.OK);
  }

  /**
   * Method to get list of all books  returned by user
   * @param username - The user for whom we want to search the returned books
   * @return - ResponseEntity object with status code and object of transaction response with details of transaction
   */

  @GetMapping("/returned/{username}")
  public ResponseEntity<List<TransactionResponse>> getAllReturnedBooksByUserId(@PathVariable String username) {
    logger.info("Get all returned books request received for User: " + username);
    List<TransactionResponse> returnedBookIsbnsByTransaction = transactionService.getAllReturnedBooksByUsername(username);
    return new ResponseEntity<>(returnedBookIsbnsByTransaction, HttpStatus.OK);
  }

  /**
   * To get all transactions made by the user, the ones where books were loan
   * @param username - The user for whom we want to search the transactions
   * @return - ResponseEntity object with status code and object of transaction class containing details of transaction
   */
  @GetMapping("/user/{username}")
  public ResponseEntity<List<Transaction>> getAllTransactionByUserId(@PathVariable String username) {
    logger.info("Get all transactions request received for User: " + username);
    List<Transaction> transactions = transactionService.getAllTransactionsByUsername(username);
    return new ResponseEntity<>(transactions, HttpStatus.OK);
  }

  /**
   * Fetch transaction with id
   * @param transactionId - The transaction we want to fetch based on id
   * @return - ResponseEntity object with status code and object of transaction class containing details of transaction
   */
  @GetMapping("/{transactionId}")
  public ResponseEntity<Transaction> getTransactionByTransactionId(@PathVariable Long transactionId) {
    logger.info("Get transaction request received for Transaction(ID): " + transactionId);
    Transaction transaction = transactionService.getTransactionByTransactionId(transactionId);
    return new ResponseEntity<>(transaction, HttpStatus.OK);
  }
}
