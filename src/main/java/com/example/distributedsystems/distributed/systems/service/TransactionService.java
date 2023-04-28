package com.example.distributedsystems.distributed.systems.service;

import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosScenario;
import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.model.transaction.Transaction;
import com.example.distributedsystems.distributed.systems.model.transaction.TransactionResponse;
import com.example.distributedsystems.distributed.systems.repository.TransactionInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import jakarta.transaction.Transactional;

/**
 * services for all the transactions
 */
@Service
public class TransactionService {
  private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

  @Autowired
  private TransactionInterface transactionInterface;

  @Autowired
  private BookService bookService;

  @Autowired
  private CartService cartService;

  /**
   *
   * @return all the transactions
   */
  public List<Transaction> getAllTransactions() {
    return (List<Transaction>) transactionInterface.findAll();
  }

  /**
   *
   * @param username
   * @return all the transactions for a given username
   */
  public List<Transaction> getAllTransactionsByUsername(String username) {
    return transactionInterface.findAllByUsername(username);
  }

  /**
   *
   * @param transactionId
   * @return returns all the transactions for a given transactionId
   */
  public Transaction getTransactionByTransactionId(Long transactionId) {
    return transactionInterface.getTransactionByTransactionId(transactionId);
  }

  /**
   *
   * @param transactionId
   * @param isbn unique id of the book to be returned
   *  it updates the status of the loaned book for a transaction as returned
   */
  @Transactional
  public void updateBookReturnedByTransactionId(Long transactionId, Long isbn) {
    bookService.updateBookInventoryByIsbn(isbn, PaxosScenario.RETURN.toString());
    transactionInterface.updateBookStatus(transactionId, isbn);
  }

  /**
   *
   * @param username
   * @return all the books that are unreturned by a given username
   */
  public List<TransactionResponse> getAllUnreturnedBooksByUsername(String username) {
    return transactionInterface.getUnreturnedBookIdsByUsername(username);
  }

  /**
   *
   * @param username
   * @return all the books that are returned for a given username
   */
  public List<TransactionResponse> getAllReturnedBooksByUsername(String username) {
    return transactionInterface.getReturnedBookIdsByUsername(username);
  }

  /**
   *
   * @param transaction object that stores the tranasaction id, username and the books that are loaned
   * @return it creates a transaction whenver a book is loaned
   */
  @Transactional
  public Transaction createTransaction(Transaction transaction) {
    for (Long isbn: transaction.getBookLoans()) {
      Book book = bookService.getBookByIsbn(isbn);
      if (book.getInventory() > 0) {
        bookService.updateBookInventoryByIsbn(isbn, PaxosScenario.LOAN.toString());
      } else {
        logger.error("Cannot loan book! Book " + isbn + "is not in stock!");
      }
    }
    Transaction savedTransaction = transactionInterface.save(transaction);
    cartService.deleteCartByUsername(transaction.getUsername());
    return savedTransaction;
  }

}
