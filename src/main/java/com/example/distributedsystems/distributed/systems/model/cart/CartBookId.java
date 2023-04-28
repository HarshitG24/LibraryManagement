package com.example.distributedsystems.distributed.systems.model.cart;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;


@Embeddable
public class CartBookId implements Serializable {
  @Column(name = "cart_id")
  private Integer cartId;

  @Column(name = "book_id")
  private Long bookId;

  /**
   * stores the cart id and book id
   * @param cartId
   * @param bookId
   */
  public CartBookId(Integer cartId, Long bookId) {
    this.cartId = cartId;
    this.bookId = bookId;
  }

  public  CartBookId() {

  }

  public Integer getCartId() {
    return cartId;
  }

  public void setCartId(Integer cartId) {
    this.cartId = cartId;
  }

  public Long getBookId() {
    return bookId;
  }

  public void setBookId(Long bookId) {
    this.bookId = bookId;
  }
}
