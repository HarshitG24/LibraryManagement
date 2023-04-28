package com.example.distributedsystems.distributed.systems.service;

import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosScenario;
import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.repository.BookInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BookService {
  @Autowired
  private BookInterface bookInterface;

  /**
   *
   * @return returns all the books in the database
   */
  public List<Book> getAllBooks() {
    return (List<Book>) bookInterface.findAll();
  }

  /**
   *
   * @param e Book object that contains all the detals about the book
   * @return saves the book in the databsase
   */
  public Book createBook(Book e) {
    return bookInterface.save(e);
  }

  /**
   *
   * @param isbn books isbn(unique id)
   * @return return the book with the given isbn
   */

  public Book getBookByIsbn(Long isbn) {
    return bookInterface.getBookByIsbn(isbn);
  }

  /**
   *
   * @param isbn books isbn(unique id)
   *   deletes the book from the database
   */
  public void deleteBookByIsbn(Long isbn) {
    bookInterface.deleteAllByIsbn(isbn);
  }

  /**
   *
   * @param isbn books isbn(unique id)
   * @param operation type of operation to be performed(loan, return)
   * @return update the count of books in the inventory according to the operation given
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
