package com.example.distributedsystems.distributed.systems.model;

import jakarta.persistence.*;

@Entity
@Table(name = "server")
public class Server {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer Id;

  private int port;


  public Server() {
  }

  public Server(int port) {
    this.port = port;
  }

  public int getId() {
    return Id;
  }

  public void setId(int id) {
    Id = id;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

}
