package com.example.distributedsystems.distributed.systems.model;

import java.util.List;


public class TransactionRequest {
  private Long userId;
  private List<Long> bookIds;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public List<Long> getBookIds() {
    return bookIds;
  }

  public void setBookIds(List<Long> bookIds) {
    this.bookIds = bookIds;
  }
}
