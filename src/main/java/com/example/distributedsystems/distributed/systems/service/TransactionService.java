package com.example.distributedsystems.distributed.systems.service;

import com.example.distributedsystems.distributed.systems.model.Transaction;
import com.example.distributedsystems.distributed.systems.repository.TransactionInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

  @Autowired
  private TransactionInterface transactionInterface;

  public List<Transaction> getAllTransactions() {
    return (List<Transaction>) transactionInterface.findAll();
  }

  public List<Transaction> getAllTransactionsByUserId(Long userId) {
    return transactionInterface.getAllByUserId(userId);
  }

  public Transaction getTransactionByTransactionId(Long transactionId) {
    return transactionInterface.getTransactionByTransactionId(transactionId);
  }

  public void updateBookReturnedByTransactionId(Long transactionId, Long bookId) {
    transactionInterface.updateBookStatus(transactionId, bookId);
  }

  public List<Long> getAllUnreturnedBooksByUserId(Long userId) {
    return transactionInterface.getUnreturnedBookIdsByUserId(userId);
  }

  public List<Long> getAllReturnedBooksByUserId(Long userId) {
    return transactionInterface.getReturnedBookIdsByUserId(userId);
  }

  public Transaction createTransaction(Transaction transaction) {
    return transactionInterface.save(transaction);
  }

}
