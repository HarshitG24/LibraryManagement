package com.example.distributedsystems.distributed.systems.service;

import com.example.distributedsystems.distributed.systems.model.Server;

import com.example.distributedsystems.distributed.systems.repository.TwoPCRestInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TwoPCRestService {
  @Autowired
  private TwoPCRestInterface twoPCRestInterface;

  public List<Server> listAllServer(){
    return (List<Server>) twoPCRestInterface.findAll();
  }
}