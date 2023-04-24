package com.example.distributedsystems.distributed.systems.dsalgo.paxos;

import java.util.List;

public class PaxosTransaction {

    private Long transactionId;
    private String userId;

    private List<Long> allBooks;

    private PaxosScenario scenario;

    private long proposalId;

    public PaxosTransaction(){}

    public PaxosTransaction(Long transactionId, String userId, List<Long> allBooks, PaxosScenario scenario) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.allBooks = allBooks;
        this.scenario = scenario;
    }

    public PaxosTransaction(String userId, List<Long> allBooks, PaxosScenario scenario) {
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

    public long getProposalId() {
        return proposalId;
    }

    public void setProposalId(long proposalId) {
        this.proposalId = proposalId;
    }
}
