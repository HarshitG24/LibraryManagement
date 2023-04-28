package com.example.distributedsystems.distributed.systems.dsalgo.paxos;

import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * The class acting as model for requests with paxos
 */
public class PaxosTransaction {

    @Id
    private Long transactionId; // the unique id
    private String username; // username of user

    private List<Long> allBooks; // list of all books

    private PaxosScenario scenario; // scenario where paxos used

    private long proposalId; // the proposal id with the request

    public PaxosTransaction(){}

    /**
     * Constructor
     * @param transactionId - The id of the transaction
     * @param username - of the user
     * @param allBooks - list of all books
     * @param scenario - the scenario under consideration
     */
    public PaxosTransaction(Long transactionId, String username, List<Long> allBooks, PaxosScenario scenario) {
        this.transactionId = transactionId;
        this.username = username;
        this.allBooks = allBooks;
        this.scenario = scenario;
    }

    /**
     * Constructor
     * @param transactionId - The id of the transaction
     * @param allBooks - list of all books
     * @param scenario - the scenario under consideration
     */
    public PaxosTransaction(Long transactionId, List<Long> allBooks, PaxosScenario scenario) {
        this.transactionId = transactionId;
        this.allBooks = allBooks;
        this.scenario = scenario;
    }

    /**
     * Constructor
     * @param username - of the user
     * @param allBooks - list of all books
     * @param scenario - the scenario under consideration
     */
    public PaxosTransaction(String username, List<Long> allBooks, PaxosScenario scenario) {
        this.username = username;
        this.allBooks = allBooks;
        this.scenario = scenario;
    }

    /**
     * Constructor
     * @param username - of the user
     * @param scenario - the scenario under consideration
     */

    public PaxosTransaction(String username, PaxosScenario scenario) {
        this.username = username;
        this.scenario = scenario;
    }

    /**
     * To get the transaction id
     * @return - the transaction id
     */
    public Long getTransactionId() {
        return transactionId;
    }

    /**
     * To set the transaction id
     * @param transactionId - the value we set it to
     */
    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * To get the username
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * To set the username
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * To get the list of all books
     * @return
     */
    public List<Long> getAllBooks() {
        return allBooks;
    }

    /**
     * To set all books
     * @param allBooks
     */
    public void setAllBooks(List<Long> allBooks) {
        this.allBooks = allBooks;
    }

    /**
     * To get the scenario
     * @return
     */
    public PaxosScenario getScenario() {
        return scenario;
    }

    /**
     * To set scenario
     * @param scenario
     */
    public void setScenario(PaxosScenario scenario) {
        this.scenario = scenario;
    }

    /**
     * To get the proposal id
     * @return
     */
    public long getProposalId() {
        return proposalId;
    }

    /**
     * To set proposal id
     * @param proposalId
     */
    public void setProposalId(long proposalId) {
        this.proposalId = proposalId;
    }

    /**
     *
     * @return - to get all values in the object
     */
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
