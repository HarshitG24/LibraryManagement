package com.example.distributedsystems.distributed.systems.model.transaction;

import java.util.List;


public class TransactionRequest {
  private Long transactionId;
  private String username;
  private List<Long> bookIds;

  public Long getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(Long transactionId) {
    this.transactionId = transactionId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public List<Long> getBookIds() {
    return bookIds;
  }

  public void setBookIds(List<Long> bookIds) {
    this.bookIds = bookIds;
  }

  @Override
  public String toString() {
    return "TransactionRequest{" +
            "transactionId=" + transactionId +
            ", username='" + username + '\'' +
            ", bookIds=" + bookIds +
            '}';
  }
}
