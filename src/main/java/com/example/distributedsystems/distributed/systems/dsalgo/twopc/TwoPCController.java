package com.example.distributedsystems.distributed.systems.dsalgo.twopc;

import com.example.distributedsystems.distributed.systems.coordinator.RestService;
import com.example.distributedsystems.distributed.systems.model.Response;
import com.example.distributedsystems.distributed.systems.model.user.User;
import com.example.distributedsystems.distributed.systems.node.NodeManager;
import com.example.distributedsystems.distributed.systems.node.NodeRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Controller
public class TwoPCController {
    @Autowired
    RestService restService;

    @Autowired
    NodeRegistry nodeRegistry;

    int ackCount  = 0;

//    @Value("${server.port}")
//    private int serverPort;

    @Autowired
    private ServerProperties serverProperties;

    public  ResponseEntity<Response> performTransaction(User emp) {
        System.out.println("entered user is: " + emp);
        try{
            List<Integer> allPorts = new ArrayList<>();

            System.out.println("port is: " + serverProperties.getPort());

            Set<String> ports = nodeRegistry.getActiveNodes();
            for(String a:ports){
                System.out.println("port address is:"+a);
            }

//            List<LinkedHashMap<String, Object>> server_list = (List<LinkedHashMap<String, Object>>) restService.get(restService.generateURL("localhost", serverProperties.getPort(), "server","allServers"), null).getBody();
//            for(LinkedHashMap<String, Object> a: server_list){
////                for(Map.Entry<String, Object> en: a.entrySet()){
////                    System.out.println(" key: " + en.getKey() + ", value is: " +  en.getValue());
////                }
//                allPorts.add(Integer.parseInt(a.get("port").toString()));
//
//                System.out.println("port number is: " + a.get("port"));
//            }

            ExecutorService executor = Executors.newFixedThreadPool(10);

            // ack phase
            for(String a:ports){


                executor.execute(() -> {
                    boolean acks = (boolean) restService.get(a+"/server/cancommit", null).getBody();

                    System.out.println("qaz: " +  acks);

                    if(acks){
                        ackCount++;
                    }
                });
            }

            executor.shutdown(); // To execute the above tasks, which is to send the commit message to all the replica together and not one after other.
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS); // We perform this blocking operation to finish the execution of the above tasks

            } catch (InterruptedException e) {
                return new ResponseEntity<>(new Response(false, "TwoPC Failed to reach consensus for the proposal"), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if(ackCount != ports.size()){
                System.out.println("fail to reach consensus");
                return new ResponseEntity<>(new Response(false, "TwoPC Failed to reach consensus for the proposal"), HttpStatus.INTERNAL_SERVER_ERROR);

            }

            System.out.println("TwoPC consesus reached");
            // commit phase as consesnus acheived

            executor = Executors.newFixedThreadPool(10);

            for(String a:ports){


                executor.execute(() -> {
                    boolean acks = (boolean) restService.post(a+"/server/docommit", emp).getBody();

//                    if(acks){
//                        System.out.println("saved data on port: " + p);
//                    }
                });
            }

            executor.shutdown();// To execute the above tasks, which is to send the commit message to all the replica together and not one after other.
            System.out.println("TwoPC commit successful");
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS); // We perform this blocking operation to finish the execution of the above tasks
            } catch (InterruptedException e) {
                System.out.println("TwoPC commit failed, Rolling back");
                return new ResponseEntity<>(new Response(false, "TwoPC Failed to commit, now rolling back..."), HttpStatus.INTERNAL_SERVER_ERROR);


            }

            ackCount = 0;

        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, "TwoPC Failed to commit, now rolling back..."), HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return new ResponseEntity<>(new Response(true, "TwoPC committed"), HttpStatus.OK);

    }



}
