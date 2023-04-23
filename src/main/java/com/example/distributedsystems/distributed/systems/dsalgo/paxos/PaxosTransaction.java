package com.example.distributedsystems.distributed.systems.dsalgo.paxos;

import java.util.List;

public class PaxosTransaction {

    private Long transactionId;
    private String userId;

    private List<Long> allBooks;

    private PaxosScenario scenario;

    public PaxosTransaction(Long transactionId, String userId, List<Long> allBooks, PaxosScenario scenario) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.allBooks = allBooks;
        this.scenario = scenario;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Long> getAllBooks() {
        return allBooks;
    }

    public void setAllBooks(List<Long> allBooks) {
        this.allBooks = allBooks;
    }

    public PaxosScenario getScenario() {
        return scenario;
    }

    public void setScenario(PaxosScenario scenario) {
        this.scenario = scenario;
    }
}
