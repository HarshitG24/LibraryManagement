package com.example.distributedsystems.distributed.systems.repository;

import com.example.distributedsystems.distributed.systems.model.transaction.Transaction;
import com.example.distributedsystems.distributed.systems.model.transaction.TransactionResponse;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import jakarta.transaction.Transactional;

/**
 * Interface for managing Transaction entities in the system.
 */
@Repository
public interface TransactionInterface extends CrudRepository<Transaction, Long> {

  /**
   * Retrieves all transactions by a specified username.
   *
   * @param username The username to filter transactions by.
   * @return A list of transactions by the specified username.
   */
  List<Transaction> findAllByUsername(String username);

  /**
   * Retrieves a transaction by its transaction ID.
   *
   * @param transactionId The ID of the transaction.
   * @return The transaction with the specified ID.
   */
  Transaction getTransactionByTransactionId(Long transactionId);

  /**
   * Updates the book status in a specified transaction.
   *
   * @param transactionId The ID of the transaction.
   * @param bookId The ID of the book to update the status.
   */
  @Modifying
  @Transactional
  default void updateBookStatus(Long transactionId, Long bookId) {
    Transaction transaction = getTransactionByTransactionId(transactionId);
    transaction.updateBookStatus(bookId);
    save(transaction);
  }

  /**
   * Retrieves unreturned book IDs by a specified username.
   *
   * @param username The username to filter transactions by.
   * @return A list of unreturned book IDs by the specified username.
   */
  @Transactional
  default List<TransactionResponse> getUnreturnedBookIdsByUsername(String username) {
    List<Transaction> transactions = findAllByUsername(username);
    List<TransactionResponse> bookIsbnsByTransaction = new ArrayList<>();
    for (Transaction transaction : transactions) {
      List<Long> transactionUnreturnedBookIds = transaction.getAllUnreturnedBooks();
      List<Long> unreturnedBookIds = new ArrayList<>(transactionUnreturnedBookIds);
      if (!unreturnedBookIds.isEmpty()) {
        TransactionResponse response = new TransactionResponse(transaction.getTransactionId(), transaction.getTransactionDate(), unreturnedBookIds, username);
        bookIsbnsByTransaction.add(response);
      }
    }
    return bookIsbnsByTransaction;
  }

  /**
   * Retrieves returned book IDs by a specified username.
   *
   * @param username The username to filter transactions by.
   * @return A list of returned book IDs by the specified username.
   */
  @Transactional
  default List<TransactionResponse> getReturnedBookIdsByUsername(String username) {
    List<Transaction> transactions = findAllByUsername(username);
    List<TransactionResponse> bookIsbnsByTransaction = new ArrayList<>();
    for (Transaction transaction : transactions) {
      List<Long> transactionUnreturnedBookIds = transaction.getAllReturnedBooks();
      List<Long> returnedBookIds = new ArrayList<>(transactionUnreturnedBookIds);
      if (!returnedBookIds.isEmpty()) {
        TransactionResponse response = new TransactionResponse(transaction.getTransactionId(), transaction.getTransactionDate(), returnedBookIds, username);
        bookIsbnsByTransaction.add(response);
      }
    }
    return bookIsbnsByTransaction;
  }
}
