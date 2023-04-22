package com.example.distributedsystems.distributed.systems.model.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long transactionId;
  private Long userId;

  @ElementCollection
  private Map<Long, Boolean> bookStatus = new HashMap<>();

  public Transaction(Long userId, List<Long> bookLoanIds) {
    this.userId = userId;
    for (Long bookId: bookLoanIds) {
      this.bookStatus.put(bookId, false);
    }
  }

  public Transaction() {
  }

  public List<Long> getBookLoans() {
    return new ArrayList<>(bookStatus.keySet());
  }

  public Map<Long, Boolean> getBookStatus() {
    return new HashMap<>(bookStatus);
  }

  public void updateBookStatus(Long bookID) {
    this.bookStatus.put(bookID, true);
  }

  public Long getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(Long transactionId) {
    this.transactionId = transactionId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public List<Long> getAllUnreturnedBooks() {
    List<Long> unreturnedBookIds = new ArrayList<>();
    for (Map.Entry<Long, Boolean> entry : bookStatus.entrySet()) {
      Long bookId = entry.getKey();
      if (!entry.getValue()) {
        unreturnedBookIds.add(bookId);
      }
    }
    return unreturnedBookIds;
  }

  public List<Long> getAllReturnedBooks() {
    List<Long> returnedBookIds = new ArrayList<>();
    for (Map.Entry<Long, Boolean> entry : bookStatus.entrySet()) {
      Long bookId = entry.getKey();
      if (entry.getValue()) {
        returnedBookIds.add(bookId);
      }
    }
    return returnedBookIds;
  }


  @Override
  public String toString() {
    return "Transaction{" +
            "transactionId=" + transactionId +
            ", userId=" + userId +
            ", bookStatus=" + bookStatus +
            '}';
  }
}

