package com.example.distributedsystems.distributed.systems.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookLoan")
public class BookLoan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(targetEntity = Transaction.class)
  @JoinColumn(name = "transactionId")
  private Long transactionId;
  private Long bookId;
  private boolean returned;

  public BookLoan(Long transactionId, Long bookId) {
    this.transactionId = transactionId;
    this.bookId = bookId;
    this.returned = false;
  }

  public BookLoan() {

  }

  public void setReturned(boolean returned) {
    this.returned = returned;
  }

  public Long getId() {
    return id;
  }

  public Long getTransactionId() {
    return transactionId;
  }

  public Long getBookId() {
    return bookId;
  }

  public boolean isReturned() {
    return returned;
  }

  @Override
  public String toString() {
    return "BookLoan{" +
            "id=" + id +
            ", transactionId=" + transactionId +
            ", bookId=" + bookId +
            ", returned=" + returned +
            '}';
  }
}
