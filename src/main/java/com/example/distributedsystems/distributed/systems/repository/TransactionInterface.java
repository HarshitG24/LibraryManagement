package com.example.distributedsystems.distributed.systems.repository;

import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.model.Transaction;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@Repository
public interface TransactionInterface extends CrudRepository<Transaction, Long> {
  List<Transaction> getAllByUserId(Long userId);
  Transaction getTransactionByTransactionId(Long transactionId);

  @Modifying
  @Query("UPDATE BookLoan bl SET bl.returned = true WHERE bl.transactionId = :transactionId AND bl.bookId = :bookId")
  void markBookReturned(@Param("transactionId") Long transactionId, @Param("bookId") Long bookId);

  default List<Long> getUnreturnedBookIdsByUserId(Long userId) {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("");
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    List<Long> unreturnedBookIds = new ArrayList<>();
    List<Transaction> transactions = getAllByUserId(userId);
    for (Transaction transaction : transactions) {
      List<Long> transactionUnreturnedBookIds = transaction.getUnreturnedBookIds(entityManager);
      unreturnedBookIds.addAll(transactionUnreturnedBookIds);
    }
    entityManager.close();
    entityManagerFactory.close();
    return unreturnedBookIds;
  }
}
