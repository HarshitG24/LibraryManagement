package com.example.distributedsystems.distributed.systems.dsalgo.paxos;

import org.springframework.data.annotation.Id;

import java.util.List;

public class PaxosTransaction {

    @Id
    private Long transactionId;
    private String username;

    private List<Long> allBooks;

    private PaxosScenario scenario;

    private long proposalId;

    public PaxosTransaction(){}

    public PaxosTransaction(Long transactionId, String username, List<Long> allBooks, PaxosScenario scenario) {
        this.transactionId = transactionId;
        this.username = username;
        this.allBooks = allBooks;
        this.scenario = scenario;
    }

    public PaxosTransaction(Long transactionId, List<Long> allBooks, PaxosScenario scenario) {
        this.transactionId = transactionId;
        this.allBooks = allBooks;
        this.scenario = scenario;
    }

    public PaxosTransaction(String username, List<Long> allBooks, PaxosScenario scenario) {
        this.username = username;
        this.allBooks = allBooks;
        this.scenario = scenario;
    }

    public PaxosTransaction(String username, PaxosScenario scenario) {
        this.username = username;
        this.scenario = scenario;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    @Override
    public String toString() {
        return "PaxosTransaction{" +
                "transactionId=" + transactionId +
                ", username='" + username + '\'' +
                ", allBooks=" + allBooks +
                ", scenario=" + scenario +
                ", proposalId=" + proposalId +
                '}';
    }
}
