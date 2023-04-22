package com.example.distributedsystems.distributed.systems.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private List<Long> allBooks;


    public Cart(String username, List<Long> allBooks) {
        this.username = username;
        this.allBooks = allBooks;
    }

    public Cart() {

    }

    public List<Long> getAllBooks() {
        return allBooks;
    }

    public void setAllBooks(List<Long> allBooks) {
        this.allBooks = allBooks;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
