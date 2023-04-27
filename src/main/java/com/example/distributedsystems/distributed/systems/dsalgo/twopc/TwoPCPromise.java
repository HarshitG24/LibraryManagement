package com.example.distributedsystems.distributed.systems.dsalgo.twopc;

public class TwoPCPromise {



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
