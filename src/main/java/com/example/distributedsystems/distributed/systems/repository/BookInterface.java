package com.example.distributedsystems.distributed.systems.repository;

import com.example.distributedsystems.distributed.systems.model.Book;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface BookInterface extends CrudRepository<Book, Long> {
  Book getBookByIsbn(Long isbn);

  void deleteAllByIsbn(Long isbn);

  @Modifying
  @Transactional
  @Query("UPDATE Book b SET b.inventory = :inventory WHERE b.isbn = :isbn")
  int updateBookInventoryByIsbn(Long isbn, int inventory);
}
