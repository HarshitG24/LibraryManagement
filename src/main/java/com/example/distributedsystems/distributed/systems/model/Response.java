package com.example.distributedsystems.distributed.systems.model;

/**
 * helper object to pass the reponses for the paxos operations
 */
public class Response {
    private boolean isSuccess;
    private String message;
    private Long isbn;
    private Long transactionId;


    /**
     *
     * @param isSuccess success status of the operation (true/false)
     * @param message  message after execution of each operation
     */
    public Response(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    /**
     *
     * @param isSuccess
     * @param message
     * @param isbn isbn of the book for which the paxos transaction were done
     * @param transactionId transaction id  for which the paxos transactions were done
     */
    public Response(boolean isSuccess, String message, Long isbn, Long transactionId) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.isbn = isbn;
        this.transactionId = transactionId;
    }

    /**
     *
     * getters and setters
     */
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
