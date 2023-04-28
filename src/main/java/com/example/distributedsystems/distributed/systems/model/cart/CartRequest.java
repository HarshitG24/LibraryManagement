package com.example.distributedsystems.distributed.systems.model.cart;

/**
 * request from the user is stored in CartRequest Object when the user add the book to the cart

 */
public class CartRequest {
    /**
     * username that added book to the cart
     */
    private String username;
    /**
     * isbn of the book that was added
     */
    private Long isbn;



    public CartRequest(String username, Long isbn) {
        this.username = username;
        this.isbn = isbn;
    }

    /**
     * getter for username
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * setter for username
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * getter for isbn
     * @return isbn
     */

    public Long getIsbn() {
        return isbn;
    }

    /**
     * setter for isbn
     * @param isbn
     */

    public void setIsbn(Long isbn) {
        this.isbn = isbn;
    }

    @Override
    public String toString() {
        return "CartRequest{" +
                "username='" + username + '\'' +
                ", isbn=" + isbn +
                '}';
    }
}
