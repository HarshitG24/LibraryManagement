package com.example.distributedsystems.distributed.systems.repository;

import com.example.distributedsystems.distributed.systems.model.transaction.Transaction;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import jakarta.transaction.Transactional;

@Repository
public interface TransactionInterface extends CrudRepository<Transaction, Long> {
  List<Transaction> getAllByUserId(Long userId);
  Transaction getTransactionByTransactionId(Long transactionId);

  @Modifying
  @Transactional
  default void updateBookStatus(Long transactionId, Long bookId) {
    Transaction transaction = getTransactionByTransactionId(transactionId);
    transaction.updateBookStatus(bookId);
    save(transaction);
  }

  @Transactional
  default List<Long> getUnreturnedBookIdsByUserId(Long userId) {
    List<Long> unreturnedBookIds = new ArrayList<>();
    List<Transaction> transactions = getAllByUserId(userId);
    for (Transaction transaction : transactions) {
      List<Long> transactionUnreturnedBookIds = transaction.getAllUnreturnedBooks();
      unreturnedBookIds.addAll(transactionUnreturnedBookIds);
    }
    return unreturnedBookIds;
  }

  @Transactional
  default List<Long> getReturnedBookIdsByUserId(Long userId) {
    List<Long> returnedBookIds = new ArrayList<>();
    List<Transaction> transactions = getAllByUserId(userId);
    for (Transaction transaction : transactions) {
      List<Long> transactionUnreturnedBookIds = transaction.getAllReturnedBooks();
      returnedBookIds.addAll(transactionUnreturnedBookIds);
    }
    return returnedBookIds;
  }


}
