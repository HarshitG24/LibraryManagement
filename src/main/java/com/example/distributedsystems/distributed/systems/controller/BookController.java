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

/**
 * The class to expose the methods associated with books to frontend
 */
@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/book")
public class BookController extends PaxosController {

  // Making an instance of logger class to log the activities in program flow
  private static final Logger logger = LoggerFactory.getLogger(BookController.class);

  // to use methods in book repository
  @Autowired
  private BookService bookService;

  /**
   * Constructor for the class
   * @param ricartAgrawalaHandler - Object of RicartAgrawalaHandler class used with vector timestamps and paxos
   * @param vectorTimestampService - Object of VectorTimestampService class used with  paxos and ricart-agrawala
   */
  @Autowired
  public BookController(RicartAgrawalaHandler ricartAgrawalaHandler, VectorTimestampService vectorTimestampService) {
    super(ricartAgrawalaHandler, vectorTimestampService);
    System.out.println("Book controller");
    System.out.println("VCT:" + vectorTimestampService);
  }

  /**
   * Method to return all the books from database
   * @return - ResponseEntity object with status code and list of all books
   */
  @GetMapping("")
  public ResponseEntity<List<Book>> getAllBooks() {
    logger.info("Get all books request received.");
    List<Book> books = bookService.getAllBooks();
    return new ResponseEntity<>(books, HttpStatus.OK);
  }

  /**
   * Method to fetch book by isbn
   * @param isbn - the id of the book which we need to fetch
   * @return - ResponseEntity object with status code and the books
   */
  @GetMapping("/{isbn}")
  public ResponseEntity<Book> getBookByIsbn(@PathVariable("isbn") Long isbn) {
    logger.info("Get book by ISBN request received. ISBN: " + isbn);
    Book book = bookService.getBookByIsbn(isbn);
    return new ResponseEntity<>(book, HttpStatus.OK);
  }

  /**
   * Method to add book to database
   * @param book - The details of the book in form of Book class instance, which needs to be added to database
   * @return - ResponseEntity object with status code and the books
   */
  @PostMapping("/createBook")
  public ResponseEntity<Book> createBook(@RequestBody Book book) {
    logger.info("Create book request received. Book: " + book);
    Book result = bookService.createBook(book);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  /**
   * Method to update inventory of book depending upon if it was loan or returned
   * @param isbn - the book for which we need to update inventory
   * @param operation - it can be loan or return
   * @return - ResponseEntity object with status code
   */
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

}
