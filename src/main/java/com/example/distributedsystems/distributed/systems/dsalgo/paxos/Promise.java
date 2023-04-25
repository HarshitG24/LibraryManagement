package com.example.distributedsystems.distributed.systems.dsalgo.paxos;

public class Promise {

    private final boolean didPromise; // to determine if the promise was accepted or not
    private final long proposalId; // to determine the proposal id which was accepted

    /**
     * Constructor
     * @param didPromise - boolean if promise was accepted
     * @param proposalId - the id which was accepted
     */
    public Promise(boolean didPromise, long proposalId) {
        this.didPromise = didPromise;
        this.proposalId = proposalId;
    }

    /**
     * getter method to return the value of promise accepted or not
     * @return true if promised, otherwise false
     */
    public boolean isDidPromise() {
        return didPromise;
    }

    /**
     * getter method to return the  proposal id
     * @return proposal id
     */
    public long getProposalId() {
        return proposalId;
    }
}

