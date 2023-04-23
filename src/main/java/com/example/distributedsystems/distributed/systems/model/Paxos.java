package com.example.distributedsystems.distributed.systems.model;

import com.example.distributedsystems.distributed.systems.model.transaction.Transaction;
import jakarta.persistence.*;

@Entity
public class Paxos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long minId;

    private @Transient Transaction transaction;

    public Paxos(){}

    public Paxos(Long minId, Transaction transaction) {
        this.minId = minId;
        this.transaction = transaction;
    }

    public Long getMinId() {
        return minId;
    }

    public void setMinId(Long minId) {
        this.minId = minId;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

