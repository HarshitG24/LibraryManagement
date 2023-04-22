package com.example.distributedsystems.distributed.systems.service;

import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.model.cart.Cart;
import com.example.distributedsystems.distributed.systems.model.cart.CartBook;
import com.example.distributedsystems.distributed.systems.model.cart.CartBookId;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CartBookInterface extends CrudRepository<CartBook, CartBookId> {

  // Query method to get a CartBook by its composite key
  Optional<CartBook> findById(CartBookId id);

  // Query method to get all CartBooks for a given cart
  List<CartBook> findByCart(Cart cart);

  // Query method to get all CartBooks for a given book
  List<CartBook> findByBook(Book book);

  // Query method to get a CartBook for a given cart and book
  Optional<CartBook> findByCartAndBook(Cart cart, Book book);
}
