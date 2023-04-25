package com.example.distributedsystems.distributed.systems.service;

import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.repository.BookInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BookService {
  @Autowired
  private BookInterface bookInterface;

  public List<Book> getAllBooks() {
    return (List<Book>) bookInterface.findAll();
  }

  public Book createBook(Book e) {
    return bookInterface.save(e);
  }

  public Book getBookByIsbn(Long isbn) {
    return bookInterface.getBookByIsbn(isbn);
  }

  public void deleteBookByIsbn(Long isbn) {
    bookInterface.deleteAllByIsbn(isbn);
  }

  public int updateBookInventoryByIsbn(Long isbn, String operation) {
    Book book = bookInterface.getBookByIsbn(isbn);
    int inventory = book.getInventory();
    if (operation.equalsIgnoreCase("LOAN")) {
      inventory = -1;
    } else if (operation.equalsIgnoreCase("RETURN")) {
      inventory = 1;
    }
    return bookInterface.updateBookInventoryByIsbn(isbn, inventory);
  }
}
