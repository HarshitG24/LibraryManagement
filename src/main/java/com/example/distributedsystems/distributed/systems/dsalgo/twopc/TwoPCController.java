package com.example.distributedsystems.distributed.systems.dsalgo.twopc;

import com.example.distributedsystems.distributed.systems.coordinator.RestService;
import com.example.distributedsystems.distributed.systems.model.Employee;
import com.example.distributedsystems.distributed.systems.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class TwoPCController {
    @Autowired
    RestService restService;

    int ackCount  = 0;

//    @Value("${server.port}")
//    private int serverPort;

    @Autowired
    private ServerProperties serverProperties;

    public  ResponseEntity<Object> performTransaction(User emp) {
        try{
            List<Integer> allPorts = new ArrayList<>();

            System.out.println("port is: " + serverProperties.getPort());

            List<LinkedHashMap<String, Object>> server_list = (List<LinkedHashMap<String, Object>>) restService.get(restService.generateURL("localhost", serverProperties.getPort(), "server","allServers"), null).getBody();
            for(LinkedHashMap<String, Object> a: server_list){
//                for(Map.Entry<String, Object> en: a.entrySet()){
//                    System.out.println(" key: " + en.getKey() + ", value is: " +  en.getValue());
//                }
                allPorts.add(Integer.parseInt(a.get("port").toString()));

                System.out.println("port number is: " + a.get("port"));
            }

            ExecutorService executor = Executors.newFixedThreadPool(10);

            // ack phase
            for (int i=0; i<allPorts.size(); i++) {
                Integer p = allPorts.get(i);

                executor.execute(() -> {
                    boolean acks = (boolean) restService.get(restService.generateURL("localhost", p, "server","cancommit"), null).getBody();

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
                throw new RuntimeException(e);
            }

            if(ackCount != server_list.size()){
                System.out.println("fail to reach consensus");
            }

            // commit phase as consesnus acheived

            executor = Executors.newFixedThreadPool(10);

            for (int i=0; i<allPorts.size(); i++) {
                Integer p = allPorts.get(i);

                executor.execute(() -> {
                    boolean acks = (boolean) restService.post(restService.generateURL("localhost", p, "server","docommit"), null).getBody();

                    if(acks){
                        System.out.println("saved data on port: " + p);
                    }
                });
            }

            executor.shutdown(); // To execute the above tasks, which is to send the commit message to all the replica together and not one after other.
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS); // We perform this blocking operation to finish the execution of the above tasks
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            ackCount = 0;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }



}
