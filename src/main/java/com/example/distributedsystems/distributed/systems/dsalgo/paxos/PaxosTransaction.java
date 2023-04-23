package com.example.distributedsystems.distributed.systems.dsalgo.paxos;

public class PaxosTransaction {

    private Long transactionId;
    private Long userId;

    public PaxosTransaction(Long transactionId, Long userId) {
        this.transactionId = transactionId;
        this.userId = userId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
