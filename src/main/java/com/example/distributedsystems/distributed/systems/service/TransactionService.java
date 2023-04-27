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

@Service
public class TransactionService {
  private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

  @Autowired
  private TransactionInterface transactionInterface;

  @Autowired
  private BookService bookService;

  @Autowired
  private CartService cartService;

  public List<Transaction> getAllTransactions() {
    return (List<Transaction>) transactionInterface.findAll();
  }

  public List<Transaction> getAllTransactionsByUsername(String username) {
    return transactionInterface.findAllByUsername(username);
  }

  public Transaction getTransactionByTransactionId(Long transactionId) {
    return transactionInterface.getTransactionByTransactionId(transactionId);
  }

  @Transactional
  public void updateBookReturnedByTransactionId(Long transactionId, Long isbn) {
    bookService.updateBookInventoryByIsbn(isbn, PaxosScenario.RETURN.toString());
    transactionInterface.updateBookStatus(transactionId, isbn);
  }

  public List<TransactionResponse> getAllUnreturnedBooksByUsername(String username) {
    return transactionInterface.getUnreturnedBookIdsByUsername(username);
  }

  public List<TransactionResponse> getAllReturnedBooksByUsername(String username) {
    return transactionInterface.getReturnedBookIdsByUsername(username);
  }

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
