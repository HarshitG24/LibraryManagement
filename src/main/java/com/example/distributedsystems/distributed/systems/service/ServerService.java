package com.example.distributedsystems.distributed.systems.service;

import com.example.distributedsystems.distributed.systems.model.Employee;
import com.example.distributedsystems.distributed.systems.model.Server;
import com.example.distributedsystems.distributed.systems.repository.EmployeeInterface;
import com.example.distributedsystems.distributed.systems.repository.ServerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServerService {
    @Autowired
    private ServerInterface serverInterface;

    public List<Server> listAllServer(){
        return (List<Server>) serverInterface.findAll();
    }
}
