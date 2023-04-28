package com.example.distributedsystems.distributed.systems.model.transaction;

import java.util.ArrayList;
import java.util.Date;
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

/**
 * model for storing all the transactions
 */
@Entity
@Table(name = "transaction")
public class Transaction {

  /**
   * transactionId: unique Id for each transaction
   * username: username for the transactions
   * transactionDate: date of the transaction
   */
  @Id
  private Long transactionId;
  private String username;
  private Long transactionDate;

  /**
   * stores status of the loaned books (returned or unreturned)
   */
  @ElementCollection
  private final Map<Long, Boolean> bookStatus = new HashMap<>();

  public Transaction(Long transactionId, String username, List<Long> bookLoanIds) {
    this.transactionId = transactionId;
    this.username = username;
    for (Long bookId: bookLoanIds) {
      this.bookStatus.put(bookId, false);
    }
    this.transactionDate = new Date().getTime();
  }

  public Transaction() {
  }
//getters for transaction date
  public Long getTransactionDate() {
    return transactionDate;
  }

  //setters for transaction date
  public void setTransactionDate(Long transactionDate) {
    this.transactionDate = transactionDate;
  }

  //getter for transaction id
  public void setTransactionId(Long transactionId) {
    this.transactionId = transactionId;
  }

  //return the list of loan book ids
  public List<Long> getBookLoans() {
    return new ArrayList<>(bookStatus.keySet());
  }

  // retrns the status of each book
  public Map<Long, Boolean> getBookStatus() {
    return new HashMap<>(bookStatus);
  }

  //update the book status to returned  (initially false(books are not returned))
  public void updateBookStatus(Long bookID) {
    this.bookStatus.put(bookID, true);
  }

  //getter for transactionId
  public Long getTransactionId() {
    return transactionId;
  }

  //getter for username
  public String getUsername() {
    return username;
  }
//setter for username
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   *
   * @return the list of isbn of all unreturned book
   */
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

  /**
   *
   * @return the list of all isbn of all returned book
   */
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
            ", username=" + username +
            ", bookStatus=" + bookStatus +
            '}';
  }
}

