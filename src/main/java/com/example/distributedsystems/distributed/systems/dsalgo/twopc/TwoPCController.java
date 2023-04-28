package com.example.distributedsystems.distributed.systems.dsalgo.twopc;

import com.example.distributedsystems.distributed.systems.coordinator.RestService;
import com.example.distributedsystems.distributed.systems.model.user.User;
import com.example.distributedsystems.distributed.systems.node.NodeRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * controller to handle twopc operations
 */

@Controller
public class TwoPCController {
    private static final Logger logger = LoggerFactory.getLogger(TwoPCController.class);

    @Autowired
    RestService restService;

    @Autowired
    NodeRegistry nodeRegistry;

    /**
     * keep track of consensus counts from all the nodes
     */
    int ackCount  = 0;

    /**
     *
     * @param emp user object that contains all the user details
     * @return ResponseEntity with appropriate message
     */
    public  ResponseEntity<TwoPCPromise> performTransaction(User emp) {
        System.out.println("entered user is: " + emp);
        /**
         * get all the list of all the active nodes
         */
        try{

            Set<String> ports = nodeRegistry.getActiveNodes();
            for(String a:ports){
                System.out.println("port address is:"+a);
            }

            /**
             * sends the proposal request to all the servers and get their consensus
             */
            ExecutorService executor = Executors.newFixedThreadPool(10);
            // Acknowledgement phase
            for(String a:ports){
                executor.execute(() -> {
                    boolean acks = (boolean) restService.get(a+"/server/cancommit", null).getBody();
                    //updates the ackCount if the servers replies with true
                    if(acks){
                        ackCount++;
                    }
                });
            }

            executor.shutdown(); // To execute the above tasks, which is to send the commit message to all the replica together and not one after other.
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS); // We perform this blocking operation to finish the execution of the above tasks
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                return new ResponseEntity<>(new TwoPCPromise(false, "Registration Unsuccessful"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            //if consensus was not reached then abort the operation
            if(ackCount != ports.size()){
                logger.error("TwoPC Failed to reach consensus for the proposal");
                return new ResponseEntity<>(new TwoPCPromise(false, "Registration Unsuccessful"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            logger.info("TwoPC consensus reached");

            ackCount = 0;
            /**
             * start the commit phase where the user is added to the database
             */

            AtomicReference<String> failed_msg = new AtomicReference<>();
            // commit phase as consensus achieved
            executor = Executors.newFixedThreadPool(10);
            for(String a:ports){
                executor.execute(() -> {
                    LinkedHashMap<String, Object> promise =  (LinkedHashMap<String, Object>) restService.post(a+"/server/docommit", emp).getBody();
                    // check if the operation was performed
                    if((Boolean) promise.get("didPromise")){
                        ackCount++;
                    }
                    // if failed to perform the operation then store the failure message
                    else {
                        System.out.println("failure message is : " +promise.get("message"));
                        failed_msg.set((String) promise.get("message"));
                    }

                });
            }


            executor.shutdown();// To execute the above tasks, which is to send the commit message to all the replica together and not one after other.



            logger.info("TwoPC commit successful");
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS); // We perform this blocking operation to finish the execution of the above tasks
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                return new ResponseEntity<>(new TwoPCPromise(false, "Registration Unsuccessful"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            /**
             * if commit is not successful in all the nodes then rollback to the previous state
             */
            if(ackCount != ports.size()){
                logger.error(String.valueOf(failed_msg));
                return new ResponseEntity<>(new TwoPCPromise(false,String.valueOf(failed_msg)), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            ackCount = 0;
        } catch (Exception e) {
            return new ResponseEntity<>(new TwoPCPromise(false, "Registration Unsuccessful"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // returns true and the TwoPCPromise object that contains the appropriate message if the commit was successfull
        return new ResponseEntity<>(new TwoPCPromise(true, "TwoPC committed"), HttpStatus.OK);
    }
}
