package com.example.distributedsystems.distributed.systems.repository;

import com.example.distributedsystems.distributed.systems.model.Book;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

/**
 * Interface for managing Book entities in the system.
 */
@Repository
public interface BookInterface extends CrudRepository<Book, Long> {

  /**
   * Retrieves a book by its ISBN.
   *
   * @param isbn The ISBN of the book.
   * @return The book with the specified ISBN.
   */
  Book getBookByIsbn(Long isbn);

  /**
   * Deletes all books with the specified ISBN.
   *
   * @param isbn The ISBN of the books to be deleted.
   */
  void deleteAllByIsbn(Long isbn);

  /**
   * Updates the inventory of a book by its ISBN.
   *
   * @param isbn The ISBN of the book to update the inventory.
   * @param inventory The new inventory value.
   * @return The number of rows affected by the update.
   */
  @Modifying
  @Transactional
  @Query("UPDATE Book b SET b.inventory = :inventory WHERE b.isbn = :isbn")
  int updateBookInventoryByIsbn(Long isbn, int inventory);
}
