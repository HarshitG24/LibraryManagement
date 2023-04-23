package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.coordinator.RestService;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosController;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosTransaction;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/book")
public class BookController extends PaxosController {

  @Autowired
  private BookService bookService;

  @Autowired
  private RestService restService;

  @GetMapping("")
  public ResponseEntity<List<Book>> getAllBooks() {
    List<Book> books = bookService.getAllBooks();
    return new ResponseEntity<>(books, HttpStatus.OK);
  }

  @GetMapping("/{isbn}")
  public ResponseEntity<Book> getBookByIsbn(@PathVariable("isbn") Long isbn) {
    Book book = bookService.getBookByIsbn(isbn);
    System.out.println("Book:" + book);
    return new ResponseEntity<>(book, HttpStatus.OK);
  }

  @PostMapping("/createBook")
  public ResponseEntity<Book> createBook(@RequestBody Book book) {
    System.out.println("create book: " + book);
    Book result = bookService.createBook(book);
    return new ResponseEntity<>(result, HttpStatus.OK);
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

//  @PostMapping("/testPaxos")
//  public ResponseEntity<Object> testBook(@RequestBody Book book) {
////    bookService.createBook(book);
//    List<Long> books = new ArrayList<>();
//    books.add(123456789012345L);
//    books.add(123456789012346L);
//
//    PaxosTransaction t = new PaxosTransaction(123456789012345L, 123L);
//    propose(t);
////    return restService.post(restService.generateURL("localhost", 8081, "prepare"), t);
//    return new ResponseEntity<>(HttpStatus.OK);
//  }
}
