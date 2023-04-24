package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.coordinator.RestService;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.PaxosTransaction;
import com.example.distributedsystems.distributed.systems.dsalgo.paxos.Promise;
import com.example.distributedsystems.distributed.systems.model.Paxos;
import com.example.distributedsystems.distributed.systems.model.transaction.Transaction;
import com.example.distributedsystems.distributed.systems.repository.PxRepository;
import com.example.distributedsystems.distributed.systems.service.TransactionService;
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

    @Autowired
    private TransactionService transactionService;

    private int maxIdSeen = 0;

    private long maxTimeStampSeen;

    @PostMapping("/prepare")
    public ResponseEntity<Object> prepare(@RequestBody PaxosTransaction transaction) {

        if (Math.random() <= 0.1) {
            System.out.println("Node failed at port: " + serverProperties.getPort());
            return new ResponseEntity<>(new Promise(false, Long.MIN_VALUE), HttpStatus.OK);
        }

        System.out.println("max id seen is: " + maxTimeStampSeen + ", pid is: " + transaction.getProposalId());

//        Paxos paxos = repository.getById(1);
        if (transaction.getProposalId() > maxTimeStampSeen) {
//            paxos.setMinId(transaction.getTransactionId());
//            repository.save(paxos);
            maxTimeStampSeen = transaction.getProposalId();
            System.out.println("Got Preparation request for " + transaction.getTransactionId() + " at port: " + serverProperties.getPort() + "Promised ");
            return new ResponseEntity<>(new Promise(true, transaction.getProposalId()), HttpStatus.OK);
        } else {
            System.out.println("Got Preparation request for " + transaction.getTransactionId() + " at port: " + serverProperties.getPort() + ": Denied");
            return new ResponseEntity<>(new Promise(false, Long.MIN_VALUE), HttpStatus.OK);
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<Long> accept(@RequestBody PaxosTransaction transaction) {
        if (Math.random() <= 0.1) {
            System.out.println("Node failed at port: " + serverProperties.getPort());
            return new ResponseEntity<>(Long.MIN_VALUE, HttpStatus.OK);
        }

//        Paxos paxos = repository.getById(1);
        if (transaction.getProposalId() >= maxIdSeen) {
//            paxos.setMinId(transaction.getTransactionId());
//            repository.save(paxos);
            System.out.println("Got accept request for " + transaction.getTransactionId() + " at port: " + serverProperties.getPort() + ": Accepted");
            return new ResponseEntity<>(transaction.getProposalId(), HttpStatus.OK);
        } else {
            System.out.println("Got accept request for " + transaction.getTransactionId() + " at port: " + serverProperties.getPort() + ": Rejected");
            return new ResponseEntity<>(Long.MIN_VALUE, HttpStatus.OK);
        }
    }

    @PostMapping("/learn")
    public ResponseEntity<Object> learn(@RequestBody PaxosTransaction t) {
        switch (t.getScenario()){
            case CHECKOUT:
                checkout(t);
                break;

            case RETURN:
                returnBook(t);


            default:
                return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    public void checkout(PaxosTransaction pt){
        Transaction transaction = new Transaction(pt.getTransactionId(), pt.getUserId(), pt.getAllBooks());
        Transaction savedTransaction = transactionService.createTransaction(transaction);
        transactionService.createTransaction(savedTransaction);
    }

    public void returnBook(PaxosTransaction pt){
        transactionService.updateBookReturnedByTransactionId(pt.getTransactionId(), pt.getAllBooks().get(0));
    }
}
