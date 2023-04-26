package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.dsalgo.vectortimestamps.VectorTimestampService;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosController;
import com.example.distributedsystems.distributed.systems.dsalgo.ricartagrawala.RicartAgrawalaHandler;
import com.example.distributedsystems.distributed.systems.model.Book;
import com.example.distributedsystems.distributed.systems.service.BookService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class BookController extends PaxosController {
  private static final Logger logger = LoggerFactory.getLogger(BookController.class);

  @Autowired
  private BookService bookService;

  @Autowired
  public BookController(RicartAgrawalaHandler ricartAgrawalaHandler, VectorTimestampService vectorTimestampService) {
    super(ricartAgrawalaHandler, vectorTimestampService);
    System.out.println("Book controller");
    System.out.println("VCT:" + vectorTimestampService);
  }

  @GetMapping("")
  public ResponseEntity<List<Book>> getAllBooks() {
    logger.info("Get all books request received.");
    List<Book> books = bookService.getAllBooks();
    return new ResponseEntity<>(books, HttpStatus.OK);
  }

  @GetMapping("/{isbn}")
  public ResponseEntity<Book> getBookByIsbn(@PathVariable("isbn") Long isbn) {
    logger.info("Get book by ISBN request received. ISBN: " + isbn);
    Book book = bookService.getBookByIsbn(isbn);
    return new ResponseEntity<>(book, HttpStatus.OK);
  }

  @PostMapping("/createBook")
  public ResponseEntity<Book> createBook(@RequestBody Book book) {
    logger.info("Create book request received. Book: " + book);
    Book result = bookService.createBook(book);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PutMapping("/{isbn}/{operation}")
  public ResponseEntity<Object> updateBookInventoryByIsbn(
          @PathVariable("isbn") Long isbn, @RequestParam("operation") String operation) {
    logger.info("Update book inventory request received. Operation: " + operation);
    int rowsAffected = bookService.updateBookInventoryByIsbn(isbn, operation);
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
