package com.example.distributedsystems.distributed.systems.model;

public class Response {
    private boolean ans;
    private String msg;

    private Long isbn;

    private Long transactionId;


    public Response(boolean ans, String msg) {
        this.ans = ans;
        this.msg = msg;
    }

    public Response(boolean ans, String msg, Long isbn, Long transactionId) {
        this.ans = ans;
        this.msg = msg;
        this.isbn = isbn;
        this.transactionId = transactionId;
    }

    public void setAns(boolean ans) {
        this.ans = ans;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isAns() {
        return ans;
    }

    public Long getIsbn() {
        return isbn;
    }

    public void setIsbn(Long isbn) {
        this.isbn = isbn;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
}
