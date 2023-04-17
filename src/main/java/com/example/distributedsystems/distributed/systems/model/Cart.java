package com.example.distributedsystems.distributed.systems.model;

import jakarta.persistence.*;

@Entity
@Table(name="cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private long isbn;

    private String username;

    public Cart(long isbn, String username) {
        this.isbn = isbn;
        this.username = username;
    }

    public long getIsbn() {
        return isbn;
    }

    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
