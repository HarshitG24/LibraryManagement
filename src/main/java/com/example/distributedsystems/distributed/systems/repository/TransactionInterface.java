package com.example.distributedsystems.distributed.systems.repository;

import com.example.distributedsystems.distributed.systems.model.transaction.Transaction;
import com.example.distributedsystems.distributed.systems.model.transaction.TransactionResponse;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import jakarta.transaction.Transactional;

@Repository
public interface TransactionInterface extends CrudRepository<Transaction, Long> {
  List<Transaction> findAllByUsername(String username);
  Transaction getTransactionByTransactionId(Long transactionId);

  @Modifying
  @Transactional
  default void updateBookStatus(Long transactionId, Long bookId) {
    Transaction transaction = getTransactionByTransactionId(transactionId);
    transaction.updateBookStatus(bookId);
    save(transaction);
  }

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
