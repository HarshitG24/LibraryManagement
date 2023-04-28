package com.example.distributedsystems.distributed.systems.model.cart;

import java.util.List;


public class CartBooksResponse {
  /**
   * the response object sent to client that contains the username and list of isbn of the books that were added to the cart
   */
  private String username;

  private List<Long> books;

  public CartBooksResponse(String username, List<Long> books) {
    this.username = username;
    this.books = books;
  }

  /**
   * getter for the username
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * setter for the username
   * @param username
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * getter for the books
   * @return the list of books isbn in the cart
   */
  public List<Long> getBooks() {
    return books;
  }

  /**
   * setter for the books
   * @param books list of isbn
   */
  public void setBooks(List<Long> books) {
    this.books = books;
  }

}
