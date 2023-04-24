//package com.example.distributedsystems.distributed.systems.dsalgo.paxos;
//
//import com.example.distributedsystems.distributed.systems.coordinator.RestService;
//import com.example.distributedsystems.distributed.systems.node.NodeRegistry;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.web.ServerProperties;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestBody;
//
//import java.util.*;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//public class PaxosController {
//    @Autowired
//    RestService restService;
//    @Autowired
//    private ServerProperties serverProperties;
//
//    @Autowired
//    NodeRegistry nodeRegistry;
//
//
//    int promiseAccepted = 0; // id of the proposal accepted
//
//    int nodesAccepted = 0;
//
//    int proposalId = 0;
//
//    public ResponseEntity<Boolean> propose(@RequestBody PaxosTransaction t) {
//
//        try{
//            Set<String> allPorts = nodeRegistry.getActiveNodes();  //new ArrayList<>();
//
//            String pid = System.currentTimeMillis() + serverProperties.getPort() + "";
//            System.out.println("pid is: " + pid);
//            t.setProposalId(System.currentTimeMillis() + serverProperties.getPort().longValue());
//
//            // 1. we store the active list of ports, the quorum
////            List<LinkedHashMap<String, Object>> server_list = (List<LinkedHashMap<String, Object>>) restService.get(restService.generateURL("localhost", serverProperties.getPort(), "server","allServers"), null).getBody();
////
////
////
////            for(LinkedHashMap<String, Object> a: server_list){
////                allPorts.add(Integer.parseInt(a.get("port").toString()));
////
////                System.out.println("port number is: " + a.get("port"));
////            }
//
////            // part 2
//            ExecutorService executor = Executors.newFixedThreadPool(10);
//
//            for (String url: allPorts) {
//
//                executor.execute(() -> {
//                    LinkedHashMap<String, Object> receivedPromise = (LinkedHashMap<String, Object>)restService.post(url + "/paxos/prepare", t).getBody();
//                    Promise promise = new Promise((boolean)receivedPromise.get("didPromise"), (long)receivedPromise.get("propsalId"));
//                    if(promise != null && promise.isDidPromise()){
//                        promiseAccepted++;
//                    }
//
//                });
//            }
//
//
//            executor.shutdown(); // To execute the above tasks, which is to send the commit message to all the replica together and not one after other.
//            try {
//                executor.awaitTermination(10, TimeUnit.SECONDS); // We perform this blocking operation to finish the execution of the above tasks
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//            System.out.println("Number of promised got: " + promiseAccepted + ", allports: " + allPorts.size());
//            // Failed to reach consensus
//            if(promiseAccepted <= allPorts.size()/2){
//                System.out.println("failed to reach consensus for the transaction: " + t.getTransactionId() + " in prepare phase");
//            }
//
//
//            // consensus achieved
//
//            executor = Executors.newFixedThreadPool(10);
//
//            for (String url: allPorts) {
//
//                executor.execute(() -> {
//                    long acceptedTID = (long)restService.post(url + "/paxos/accept", t).getBody();
//                    System.out.println("accepted id is: " + acceptedTID);
//                    if(acceptedTID != Long.MIN_VALUE){
//                        nodesAccepted++;
//                    }
//
//                });
//            }
//
//            System.out.println("number of nodes accepted: " + nodesAccepted + ", total: " + allPorts.size());
//
//            executor.shutdown(); // To execute the above tasks, which is to send the commit message to all the replica together and not one after other.
//            try {
//                executor.awaitTermination(10, TimeUnit.SECONDS); // We perform this blocking operation to finish the execution of the above tasks
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//            // Failed to reach consensus
//            if(nodesAccepted <= allPorts.size()/2){
//                System.out.println("failed to reach consensus for the transaction: " + t.getTransactionId() + " in accept");
//            }
//
//            executor = Executors.newFixedThreadPool(10);
//
//            // consensus achieved all nodes accepted, now we go to learn phase
//            for (String url: allPorts) {
////                Integer p = allPorts.get(i);
//
//                executor.execute(() -> {
//                    // learning phase
//                    restService.post(url + "/paxos/learn", t);
//                });
//            }
//
//            executor.shutdown(); // To execute the above tasks, which is to send the commit message to all the replica together and not one after other.
//            try {
//                executor.awaitTermination(10, TimeUnit.SECONDS); // We perform this blocking operation to finish the execution of the above tasks
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        return new ResponseEntity<>(true, HttpStatus.OK);
//    }
//}
