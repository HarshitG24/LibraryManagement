package com.example.distributedsystems.distributed.systems.service;

import com.example.distributedsystems.distributed.systems.model.transaction.Transaction;
import com.example.distributedsystems.distributed.systems.model.transaction.TransactionRequest;
import com.example.distributedsystems.distributed.systems.model.transaction.TransactionResponse;
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

  public List<Transaction> getAllTransactionsByUsername(String username) {
    return transactionInterface.findAllByUsername(username);
  }

  public Transaction getTransactionByTransactionId(Long transactionId) {
    return transactionInterface.getTransactionByTransactionId(transactionId);
  }

  public void updateBookReturnedByTransactionId(Long transactionId, Long bookId) {
    transactionInterface.updateBookStatus(transactionId, bookId);
  }

  public List<TransactionResponse> getAllUnreturnedBooksByUsername(String username) {
    return transactionInterface.getUnreturnedBookIdsByUsername(username);
  }

  public List<TransactionResponse> getAllReturnedBooksByUsername(String username) {
    return transactionInterface.getReturnedBookIdsByUsername(username);
  }

  public Transaction createTransaction(Transaction transaction) {
    return transactionInterface.save(transaction);
  }

}
