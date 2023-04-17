package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.service.BookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/book")
public class BookController {

  @Autowired
  private BookService bookService;

  @GetMapping("/")
  public ResponseEntity<List<Book>> getAllBooks() {
    List<Book> books = bookService.getAllBooks();
    System.out.println("Books:" + books);
    return new ResponseEntity<>(books, HttpStatus.OK);
  }

  @PostMapping("/")
  public ResponseEntity<Object> createBook(@RequestBody Book book) {
    bookService.createBook(book);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping("/{isbn}")
  public ResponseEntity<Object> updateBookInventoryByIsbn(
          @PathVariable("isbn") Long isbn, @RequestParam("inventory") int inventory) {
    int rowsAffected = bookService.updateBookInventoryByIsbn(isbn, inventory);
    if (rowsAffected > 0) {
      return new ResponseEntity<>(HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
}
