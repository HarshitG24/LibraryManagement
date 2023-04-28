package com.example.distributedsystems.distributed.systems.model;

import com.example.distributedsystems.distributed.systems.model.transaction.Transaction;
import jakarta.persistence.*;

/**
 * entity for storing the necessary ids for the paxos transaction
 */
@Entity
public class Paxos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long minId;

    private @Transient Transaction transaction;

    public Paxos(){}

    /**
     *
     * @param minId the id for the paxos operation
     * @param transaction the transaction object
     */
    public Paxos(Long minId, Transaction transaction) {
        this.minId = minId;
        this.transaction = transaction;
    }

    /**
     * getter and setters
     *
     */
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

