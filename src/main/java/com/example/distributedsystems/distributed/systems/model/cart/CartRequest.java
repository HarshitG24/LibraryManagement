package com.example.distributedsystems.distributed.systems.model.cart;

public class CartRequest {
    private String username;
    private Long isbn;

    public CartRequest(String username, Long isbn) {
        this.username = username;
        this.isbn = isbn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getIsbn() {
        return isbn;
    }

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
