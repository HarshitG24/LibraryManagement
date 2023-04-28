package com.example.distributedsystems.distributed.systems.dsalgo.twopc;

/**
 * promise object that  contains the messages from all the phases in twoPC algorithm
 */
public class TwoPCPromise {


    /**
     * didPromise: boolean- True if the phase was successful or else fale
     * message: conatins the message from the execution of each phase
     */

        private final boolean didPromise;
        private final String message;

        public TwoPCPromise(boolean didPromise, String message) {
            this.didPromise = didPromise;
            this.message = message;
        }

        /**
         * getter method to return the value of promise accepted or not
         * @return true if promised, otherwise false
         */
        public boolean isDidPromise() {
            return didPromise;
        }

        public String getMessage() {
            return message;
        }

}
