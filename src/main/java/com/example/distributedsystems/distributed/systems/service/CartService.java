package com.example.distributedsystems.distributed.systems.service;

import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.model.cart.Cart;
import com.example.distributedsystems.distributed.systems.model.cart.CartBook;
import com.example.distributedsystems.distributed.systems.model.cart.CartBookId;
import com.example.distributedsystems.distributed.systems.repository.BookInterface;
import com.example.distributedsystems.distributed.systems.repository.CartInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

@Service
public class CartService {

  @Autowired
  CartInterface cartInterface;

  @Autowired
  BookInterface bookInterface;

  @Autowired
  CartBookInterface cartBookInterface;

  public void createCart(Cart cart) {
    cartInterface.save(cart);
  }

  public List<Long> getAllBooksInCartByUsername(String username) {
    Optional<Cart> optionalCart = Optional.ofNullable(cartInterface.findByUsername(username));
    if (optionalCart.isPresent()) {
      Cart cart = optionalCart.get();
      List<CartBook> cartBooks = cartBookInterface.findByCart(cart);
      return cartBooks.stream().map(cartBook -> cartBook.getBook().getIsbn()).collect(Collectors.toList());
    } else {
      return Collections.emptyList();
    }
  }

  @Transactional
  public void deleteCartByUsername(String username) {
    Optional<Cart> optionalCart = Optional.ofNullable(cartInterface.findByUsername(username));
    if (optionalCart.isPresent()) {
      Cart cart = optionalCart.get();
      cartInterface.delete(cart);
    }
  }


  public void updateCartForUser(String user, Long bookId) {
    // Get the cart for the user
    Optional<Cart> optionalCart = Optional.ofNullable(cartInterface.findByUsername(user));
    Cart cart;

    // If the cart exists, use it. Otherwise, create a new cart.
    if (optionalCart.isPresent()) {
      cart = optionalCart.get();
    } else {
      cart = new Cart(user);
      cartInterface.save(cart);
    }

    // Get the cart-book relationship for the given book and cart
    Optional<Book> optionalBook = bookInterface.findById(bookId);
    if (optionalBook.isPresent()) {
      Book book = optionalBook.get();
      CartBook cartBook = new CartBook(cart, book);
      cartBookInterface.save(cartBook);
    }
  }


  public void deleteBookFromCartForUser(String user, Long isbn) {
    Optional<Cart> cartOptional = Optional.ofNullable(cartInterface.findByUsername(user));
    if (cartOptional.isPresent()) {
      Cart cart = cartOptional.get();
      Optional<Book> bookOptional = Optional.ofNullable(bookInterface.getBookByIsbn(isbn));
      Book book = bookOptional.orElseThrow(() -> new RuntimeException("Book not found"));

      CartBookId cartBookId = new CartBookId(cart.getId(), book.getIsbn());
      Optional<CartBook> cartBookOptional = cartBookInterface.findById(cartBookId);
      if (cartBookOptional.isPresent()) {
        CartBook cartBook = cartBookOptional.get();
        cartBookInterface.delete(cartBook);
      }
    }
  }
}



