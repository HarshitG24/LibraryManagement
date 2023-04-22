package com.example.distributedsystems.distributed.systems.model.cart;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name="cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartBook> cartBooks = new ArrayList<>();

    public Cart(String username) {
        this.username = username;
    }

    public Cart() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<CartBook> getCartBooks() {
        return cartBooks;
    }

    public void setCartBooks(List<CartBook> cartBooks) {
        this.cartBooks = cartBooks;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
