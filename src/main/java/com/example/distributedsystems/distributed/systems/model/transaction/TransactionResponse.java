package com.example.distributedsystems.distributed.systems.model.transaction;

import java.util.List;


public class TransactionResponse {
  private Long transactionId;
  private String username;
  private List<Long> bookIsbns;
  private Long transactionDate;


  public TransactionResponse(Long transactionId, Long transactionDate, List<Long> bookIsbns, String username) {
    this.transactionId = transactionId;
    this.bookIsbns = bookIsbns;
    this.username = username;
    this.transactionDate = transactionDate;
  }

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

  public List<Long> getBookIsbns() {
    return bookIsbns;
  }

  public void setBookIsbns(List<Long> bookIsbns) {
    this.bookIsbns = bookIsbns;
  }

  public Long getTransactionDate() {
    return transactionDate;
  }

  public void setTransactionDate(Long transactionDate) {
    this.transactionDate = transactionDate;
  }

  @Override
  public String toString() {
    return "TransactionResponse{" +
            "transactionId=" + transactionId +
            ", username='" + username + '\'' +
            ", bookIsbns=" + bookIsbns +
            ", transactionDate=" + transactionDate +
            '}';
  }
}
