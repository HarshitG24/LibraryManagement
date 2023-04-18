package com.example.distributedsystems.distributed.systems.model;

import jakarta.persistence.*;

@Entity
@Table(name="User")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  private String firstName;

  private String lastName;


  private String email;

  private String password;

  private String phone;

  @Embedded
  private Address address;

  public User() {
  }

  public User(String... data)
  {
    this.firstName = data[0];
    this.lastName = data[1];
    this.email = data[2];
    this.password = data[3];
    this.phone = data[4];
    this.address.address1 = data[5];
    this.address.address2 = data[6];
    this.address.city = data[7];
    this.address.state = data[8];
    this.address.zipcode = data[9];
  }

  @Embeddable
  public static class Address {


    private String address1;

    private String address2;


    private String city;


    private String state;


    private String zipcode;
  }

  public Long getId() {
    return userId;
  }

  public void setId(long id) {

    userId =  id;
  }

  public String getFirstName() {

    return firstName;
  }

  public String getLastName() {

    return lastName;
  }

  public String getEmail() {

    return email;
  }

  public String getPassword() {

    return password;
  }

  public String getPhone() {

    return phone;
  }

  @Override
  public String toString() {
    return "User{" +
            "userId=" + userId + '\'' +
            "username=" + firstName + ' '+ lastName + '\'' +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", phone='" + phone + '\'' +
            ", address=" + address.address1 + ' ' +address.address2 + ' ' + address.city + ' '+ address.state + ' '+ address.zipcode + '\''+
            '}';
  }
}