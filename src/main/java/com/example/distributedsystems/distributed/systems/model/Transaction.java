package com.example.distributedsystems.distributed.systems.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Query;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long transactionId;
  private Long userId;
  @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
  private List<BookLoan> bookLoans;

  public Transaction(Long userId, List<Long> bookIds) {
    this.userId = userId;
    this.bookLoans = new ArrayList<>();
    for (Long bookId : bookIds) {
      this.bookLoans.add(new BookLoan(this.transactionId, bookId));
    }
  }

  public Transaction() {

  }

  public List<BookLoan> getBookLoans() {
    return bookLoans;
  }

  public void setBookLoans(List<BookLoan> bookLoans) {
    this.bookLoans = bookLoans;
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

  public List<Long> getUnreturnedBookIds(EntityManager entityManager) {
    Query query = entityManager.createQuery("SELECT bl.bookId FROM BookLoan bl WHERE bl.transactionId = :transactionId AND bl.returned = false");
    query.setParameter("transactionId", transactionId);
    List<Long> bookIds = query.getResultList();
    return new ArrayList<>(bookIds);
  }

  @Override
  public String toString() {
    return "Transaction{" +
            "transactionId=" + transactionId +
            ", userId=" + userId +
            ", bookLoans=" + bookLoans +
            '}';
  }
}

