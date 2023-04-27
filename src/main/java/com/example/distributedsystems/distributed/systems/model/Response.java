package com.example.distributedsystems.distributed.systems.model;

public class Response {
    private boolean isSuccess;
    private String message;
    private Long isbn;
    private Long transactionId;


    public Response(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public Response(boolean isSuccess, String message, Long isbn, Long transactionId) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.isbn = isbn;
        this.transactionId = transactionId;
    }

    public void setSuccess(boolean success) {
        this.isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
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
