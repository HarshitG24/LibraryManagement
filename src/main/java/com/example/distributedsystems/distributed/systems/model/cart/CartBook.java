package com.example.distributedsystems.distributed.systems.model.cart;

import com.example.distributedsystems.distributed.systems.model.Book;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "cart_books")
public class CartBook {

  @EmbeddedId
  private CartBookId id;

  @ManyToOne
  @MapsId("cartId")
  @JoinColumn(name = "cart_id")
  private Cart cart;

  @ManyToOne
  @MapsId("bookId")
  @JoinColumn(name = "book_id")
  private Book book;

  public CartBook(Cart cart, Book book) {
    this.cart = cart;
    this.book = book;
    this.id = new CartBookId(cart.getId(), book.getIsbn());
  }

  public CartBook() {

  }

  public CartBookId getId() {
    return id;
  }

  public void setId(CartBookId id) {
    this.id = id;
  }

  public Cart getCart() {
    return cart;
  }

  public void setCart(Cart cart) {
    this.cart = cart;
  }

  public Book getBook() {
    return book;
  }

  public void setBook(Book book) {
    this.book = book;
  }
}
