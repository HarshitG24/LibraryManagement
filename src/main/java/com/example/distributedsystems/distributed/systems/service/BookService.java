package com.example.distributedsystems.distributed.systems.service;

import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosScenario;
import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.repository.BookInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing books and their inventory in the system.
 */
@Service
public class BookService {

  @Autowired
  private BookInterface bookInterface;

  /**
   * Retrieves all books from the repository.
   *
   * @return a list of Book objects.
   */
  public List<Book> getAllBooks() {
    return (List<Book>) bookInterface.findAll();
  }

  /**
   * Creates and saves a new Book object in the repository.
   *
   * @param e the Book object to be saved.
   * @return the saved Book object.
   */
  public Book createBook(Book e) {
    return bookInterface.save(e);
  }

  /**
   * Retrieves a Book object from the repository based on its ISBN.
   *
   * @param isbn the ISBN of the book to be retrieved.
   * @return the retrieved Book object.
   */
  public Book getBookByIsbn(Long isbn) {
    return bookInterface.getBookByIsbn(isbn);
  }

  /**
   * Deletes a Book object from the repository based on its ISBN.
   *
   * @param isbn the ISBN of the book to be deleted.
   */
  public void deleteBookByIsbn(Long isbn) {
    bookInterface.deleteAllByIsbn(isbn);
  }

  /**
   * Updates the inventory of a Book object in the repository based on its ISBN and the given operation.
   *
   * @param isbn      the ISBN of the book to be updated.
   * @param operation the operation to be performed (PaxosScenario.LOAN or PaxosScenario.RETURN).
   * @return the number of affected rows in the repository.
   */
  public int updateBookInventoryByIsbn(Long isbn, String operation) {
    Book book = bookInterface.getBookByIsbn(isbn);
    int inventory = book.getInventory();
    if (operation.equalsIgnoreCase(PaxosScenario.LOAN.toString())) {
      inventory = inventory - 1;
    } else if (operation.equalsIgnoreCase(PaxosScenario.RETURN.toString())) {
      inventory = inventory + 1;
    }
    return bookInterface.updateBookInventoryByIsbn(isbn, inventory);
  }
}
