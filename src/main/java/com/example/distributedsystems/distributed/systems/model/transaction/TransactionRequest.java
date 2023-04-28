package com.example.distributedsystems.distributed.systems.model.transaction;

import java.util.List;

/**
 * request from client side for transaction are stored in this object
 */
public class TransactionRequest {

  /**
   * transactionId: id of the transaction
   * username: username who loaned the book
   * bookIsbns: list of the Isbns of the books to be loaned
   */
  private Long transactionId;
  private String username;
  private List<Long> bookIsbns;


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

  @Override
  public String toString() {
    return "TransactionRequest{" +
            "transactionId=" + transactionId +
            ", username='" + username + '\'' +
            ", bookIsbns=" + bookIsbns +
            '}';
  }
}
