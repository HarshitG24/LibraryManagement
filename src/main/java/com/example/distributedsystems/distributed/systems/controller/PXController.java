package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.coordinator.RestService;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosTransaction;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.Promise;
import com.example.distributedsystems.distributed.systems.model.Paxos;
import com.example.distributedsystems.distributed.systems.model.transaction.Transaction;
import com.example.distributedsystems.distributed.systems.repository.PxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
@RequestMapping("/paxos")
public class PXController {

    @Autowired
    RestService restService;

    @Autowired
    PxRepository repository;

    @Autowired
    ServerProperties serverProperties;

    private long maxIdSeen = 0L;



//    @PostMapping("/propose")
//    public ResponseEntity<Boolean> propose(@RequestBody PaxosTransaction t) {
//
//
//        try{
//            List<Integer> allPorts = new ArrayList<>();
//
//            // 1. we store the active list of ports, the quorum
//            List<LinkedHashMap<String, Object>> server_list = (List<LinkedHashMap<String, Object>>) restService.get(restService.generateURL("localhost", serverProperties.getPort(), "server","allServers"), null).getBody();
//            for(LinkedHashMap<String, Object> a: server_list){
//                allPorts.add(Integer.parseInt(a.get("port").toString()));
//
//                System.out.println("port number is: " + a.get("port"));
//            }
//
////            // part 2
////            ExecutorService executor = Executors.newFixedThreadPool(10);
////
////            for (int i=0; i<allPorts.size(); i++) {
////                Integer p = allPorts.get(i);
////
////                executor.execute(() -> {
////                    LinkedHashMap<String, Object> receivedPromise = (LinkedHashMap<String, Object>)restService.post(restService.generateURL("localhost", p, "prepare"), t).getBody();
////
////                    Promise promise = new Promise((boolean)receivedPromise.get("didPromise"), (long)receivedPromise.get("proposalId"));
////
////                    if(promise != null && promise.isDidPromise()){
////                        promiseAccepted++;
////                    }
////
////                });
////            }
////
////            executor.shutdown(); // To execute the above tasks, which is to send the commit message to all the replica together and not one after other.
////            try {
////                executor.awaitTermination(10, TimeUnit.SECONDS); // We perform this blocking operation to finish the execution of the above tasks
////            } catch (InterruptedException e) {
////                throw new RuntimeException(e);
////            }
////
////            // Failed to reach consensus
////            if(promiseAccepted <= allPorts.size()/2){
////                System.out.println("failed to reach consensus for the transaction: " + t.getTransactionId() + " in prepare phase");
////            }
////
////
////            // consensus achieved
////
////            executor = Executors.newFixedThreadPool(10);
////
////            for (int i=0; i<allPorts.size(); i++) {
////                Integer p = allPorts.get(i);
////
////                executor.execute(() -> {
////                    Long acceptedTID = (Long)restService.post(restService.generateURL("localhost", p, "accept"), t).getBody();
////
////                    if(acceptedTID != Long.MIN_VALUE && acceptedTID == t.getTransactionId()){
////                        nodesAccepted++;
////                    }
////
////                });
////            }
////
////            executor.shutdown(); // To execute the above tasks, which is to send the commit message to all the replica together and not one after other.
////            try {
////                executor.awaitTermination(10, TimeUnit.SECONDS); // We perform this blocking operation to finish the execution of the above tasks
////            } catch (InterruptedException e) {
////                throw new RuntimeException(e);
////            }
////
////            // Failed to reach consensus
////            if(nodesAccepted <= allPorts.size()/2){
////                System.out.println("failed to reach consensus for the transaction: " + t.getTransactionId() + " in accept");
////            }
////
////            // consensus achieved all nodes accepted, now we go to learn phase
////            for (int i=0; i<allPorts.size(); i++) {
////                Integer p = allPorts.get(i);
////
////                executor.execute(() -> {
////
////
////                });
////            }
////
////            executor.shutdown(); // To execute the above tasks, which is to send the commit message to all the replica together and not one after other.
////            try {
////                executor.awaitTermination(10, TimeUnit.SECONDS); // We perform this blocking operation to finish the execution of the above tasks
////            } catch (InterruptedException e) {
////                throw new RuntimeException(e);
////            }
//
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        return new ResponseEntity<>(true, HttpStatus.OK);
//    }

    @PostMapping("/prepare")
    public ResponseEntity<Object> prepare(@RequestBody PaxosTransaction transaction) {

        if (Math.random() <= 0.1) {
            System.out.println("Node failed at port: " + serverProperties.getPort());
            return new ResponseEntity<>(new Promise(false, Long.MIN_VALUE), HttpStatus.OK);
        }

//        Paxos paxos = repository.getById(1);
        if (transaction.getTransactionId() > maxIdSeen) {
//            paxos.setMinId(transaction.getTransactionId());
//            repository.save(paxos);
            maxIdSeen = transaction.getTransactionId();
            System.out.println("Got Preparation request for " + transaction.getTransactionId() + " at port: " + serverProperties.getPort() + "Promised ");
            return new ResponseEntity<>(new Promise(true, transaction.getTransactionId()), HttpStatus.OK);
        } else {
            System.out.println("Got Preparation request for " + transaction.getTransactionId() + " at port: " + serverProperties.getPort() + ": Denied");
            return new ResponseEntity<>(new Promise(false, 0), HttpStatus.OK);
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<Object> accept(@RequestBody PaxosTransaction transaction) {
        if (Math.random() <= 0.1) {
            System.out.println("Node failed at port: " + serverProperties.getPort());
            return new ResponseEntity<>(Long.MIN_VALUE, HttpStatus.OK);
        }

//        Paxos paxos = repository.getById(1);
        if (transaction.getTransactionId() >= maxIdSeen) {
//            paxos.setMinId(transaction.getTransactionId());
//            repository.save(paxos);
            System.out.println("Got accept request for " + transaction.getTransactionId() + " at port: " + serverProperties.getPort() + ": Accepted");
            return new ResponseEntity<>(transaction.getTransactionId(), HttpStatus.OK);
        } else {
            System.out.println("Got accept request for " + transaction.getTransactionId() + " at port: " + serverProperties.getPort() + ": Rejected");
            return new ResponseEntity<>(Long.MIN_VALUE, HttpStatus.OK);
        }
    }

    @PostMapping("/learn")
    public ResponseEntity<Object> learn(@RequestBody Transaction transaction) {
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
