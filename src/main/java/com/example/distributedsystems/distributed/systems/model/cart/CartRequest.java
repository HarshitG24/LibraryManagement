package com.example.distributedsystems.distributed.systems.model.cart;

public class CartRequest {
    private String user;
    private Long isbn;

    public CartRequest(String user, Long isbn) {
        this.user = user;
        this.isbn = isbn;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getIsbn() {
        return isbn;
    }

    public void setIsbn(Long isbn) {
        this.isbn = isbn;
    }
}
