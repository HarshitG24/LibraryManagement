package com.example.distributedsystems.distributed.systems.dsalgo.paxos;

public class Promise {

    private boolean didPromise; // to determine if the promise was accepted or not
    private long propsalId; // to determine the proposal id which was accepted

    /**
     * Constructor
     * @param didPromise - boolean if promise was accepted
     * @param propsalId - the id which was accepted
     */
    public Promise(boolean didPromise, long propsalId) {
        this.didPromise = didPromise;
        this.propsalId = propsalId;
    }

    /**
     * getter method to return the value of promise accepted or not
     * @return
     */
    public boolean isDidPromise() {
        return didPromise;
    }

    /**
     * getter method to return the  proposal id
     * @return
     */
    public long getPropsalId() {
        return propsalId;
    }
}

