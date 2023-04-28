package com.example.distributedsystems.distributed.systems.model.cart;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * model for storing the cart
 */
@Entity
@Table(name="cart")
public class Cart {

    /**
     * unique id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * username
     */
    private String username;

    /**
     * gets the list of the cartBooks
     */
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

    /**
     * setter for id
     * @param id return id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return all the cartBooks
     */

    public List<CartBook> getCartBooks() {
        return cartBooks;
    }

    /**
     *
     * @param cartBooks list of cartBook object that contains the books inside the cart
     */

    public void setCartBooks(List<CartBook> cartBooks) {
        this.cartBooks = cartBooks;
    }

    /**
     *
     * @return the username
     */

    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     * setter for username
     */

    public void setUsername(String username) {
        this.username = username;
    }
}
