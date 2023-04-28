package com.example.distributedsystems.distributed.systems.repository;

import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.model.cart.Cart;
import com.example.distributedsystems.distributed.systems.model.cart.CartBook;
import com.example.distributedsystems.distributed.systems.model.cart.CartBookId;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface for managing CartBook entities in the system.
 */
public interface CartBookInterface extends CrudRepository<CartBook, CartBookId> {

  /**
   * Retrieves a CartBook by its composite key.
   *
   * @param id the CartBookId composite key.
   * @return an Optional containing the CartBook if found, empty otherwise.
   */
  Optional<CartBook> findById(CartBookId id);

  /**
   * Retrieves all CartBook entities for a given cart.
   *
   * @param cart the Cart object to search for.
   * @return a list of CartBook objects.
   */
  List<CartBook> findByCart(Cart cart);

  /**
   * Retrieves all CartBook entities for a given book.
   *
   * @param book the Book object to search for.
   * @return a list of CartBook objects.
   */
  List<CartBook> findByBook(Book book);

  /**
   * Retrieves a CartBook entity for a given cart and book.
   *
   * @param cart the Cart object to search for.
   * @param book the Book object to search for.
   * @return an Optional containing the CartBook if found, empty otherwise.
   */
  Optional<CartBook> findByCartAndBook(Cart cart, Book book);
}
