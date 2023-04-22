package com.example.distributedsystems.distributed.systems.model.cart;

import java.util.List;

public class CartBooksResponse {

  private String username;

  private List<Long> books;

  public CartBooksResponse(String username, List<Long> books) {
    this.username = username;
    this.books = books;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public List<Long> getBooks() {
    return books;
  }

  public void setBooks(List<Long> books) {
    this.books = books;
  }

}
