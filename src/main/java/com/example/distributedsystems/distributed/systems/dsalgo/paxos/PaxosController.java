package com.example.distributedsystems.distributed.systems.dsalgo.paxos;

import com.example.distributedsystems.distributed.systems.coordinator.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PaxosController {
    @Autowired
    RestService restService;
    @Autowired
    private ServerProperties serverProperties;


    int promiseAccepted = 0; // id of the proposal accepted

    int nodesAccepted = 0;

    public ResponseEntity<Boolean> propose(@RequestBody PaxosTransaction t) {

        try{
            List<Integer> allPorts = new ArrayList<>();

            // 1. we store the active list of ports, the quorum
            List<LinkedHashMap<String, Object>> server_list = (List<LinkedHashMap<String, Object>>) restService.get(restService.generateURL("localhost", serverProperties.getPort(), "server","allServers"), null).getBody();



            for(LinkedHashMap<String, Object> a: server_list){
                allPorts.add(Integer.parseInt(a.get("port").toString()));

                System.out.println("port number is: " + a.get("port"));
            }

//            // part 2
            ExecutorService executor = Executors.newFixedThreadPool(10);

            for (int i=0; i<allPorts.size(); i++) {
                Integer p = allPorts.get(i);

                executor.execute(() -> {
                    LinkedHashMap<String, Object> receivedPromise = (LinkedHashMap<String, Object>)restService.post(restService.generateURL("localhost", p, "paxos", "prepare"), t).getBody();


//                    Promise pr = (Promise) restService.post(restService.generateURL("localhost", p, "paxos", "prepare"), t).getBody();

//                    System.out.println("is this null? " + (pr != null) + ", value is: " + pr.getPropsalId());
                    Promise promise = new Promise((boolean)receivedPromise.get("didPromise"), (long)receivedPromise.get("propsalId"));
//
                    if(promise != null && promise.isDidPromise()){
                        promiseAccepted++;
                    }

                });
            }


            executor.shutdown(); // To execute the above tasks, which is to send the commit message to all the replica together and not one after other.
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS); // We perform this blocking operation to finish the execution of the above tasks
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println("Number of promised got: " + promiseAccepted + ", allports: " + allPorts.size());
            // Failed to reach consensus
            if(promiseAccepted <= allPorts.size()/2){
                System.out.println("failed to reach consensus for the transaction: " + t.getTransactionId() + " in prepare phase");
            }


            // consensus achieved

            executor = Executors.newFixedThreadPool(10);

            for (int i=0; i<allPorts.size(); i++) {
                Integer p = allPorts.get(i);

                executor.execute(() -> {
                    Long acceptedTID = (Long)restService.post(restService.generateURL("localhost", p, "paxos", "accept"), t).getBody();

                    System.out.println("returned tid is: " + acceptedTID + " tid: " + t.getTransactionId() + Long.compare(acceptedTID, t.getTransactionId()));
                    if(acceptedTID != null){
                        nodesAccepted++;
                    }

                });
            }

            System.out.println("number of nodes accepted: " + nodesAccepted + ", total: " + allPorts.size());

            executor.shutdown(); // To execute the above tasks, which is to send the commit message to all the replica together and not one after other.
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS); // We perform this blocking operation to finish the execution of the above tasks
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Failed to reach consensus
            if(nodesAccepted <= allPorts.size()/2){
                System.out.println("failed to reach consensus for the transaction: " + t.getTransactionId() + " in accept");
            }

            executor = Executors.newFixedThreadPool(10);

            // consensus achieved all nodes accepted, now we go to learn phase
            for (int i=0; i<allPorts.size(); i++) {
                Integer p = allPorts.get(i);

                executor.execute(() -> {
                    // learning phase

                });
            }

            executor.shutdown(); // To execute the above tasks, which is to send the commit message to all the replica together and not one after other.
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS); // We perform this blocking operation to finish the execution of the above tasks
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}