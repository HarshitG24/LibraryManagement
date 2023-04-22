package com.example.distributedsystems.distributed.systems.model;

public class CartContent {
    private String user;
    private int bid;

    public CartContent(String user, int bid) {
        this.user = user;
        this.bid = bid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }
}
