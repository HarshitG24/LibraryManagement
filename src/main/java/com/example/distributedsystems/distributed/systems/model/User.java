package com.example.distributedsystems.distributed.systems.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;
  private String firstName;
  private String lastName;
  private String email;
  private String username;
  private String password;
  private String phone;

  @Embedded
  private Address address;

  public User() {
  }

  public User(String firstName, String lastName, String email, String password, String username,
              String phone, Address address) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
    this.username = username;
    this.phone = phone;
    this.address = address;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFirstName() {

    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {

    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {

    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public String getPassword() {

    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPhone() {

    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  @Override
  public String toString() {
    return "User{" +
            "userId=" + userId +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", phone='" + phone + '\'' +
            ", address=" + address +
            '}';
  }

  @Embeddable
  public static class Address {
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zipcode;

    public Address(String address1, String address2, String city, String state, String zipcode) {
      this.address1 = address1;
      this.address2 = address2;
      this.city = city;
      this.state = state;
      this.zipcode = zipcode;
    }

    public Address() {

    }

    public String getAddress1() {
      return address1;
    }

    public void setAddress1(String address1) {
      this.address1 = address1;
    }

    public String getAddress2() {
      return address2;
    }

    public void setAddress2(String address2) {
      this.address2 = address2;
    }

    public String getCity() {
      return city;
    }

    public void setCity(String city) {
      this.city = city;
    }

    public String getState() {
      return state;
    }

    public void setState(String state) {
      this.state = state;
    }

    public String getZipcode() {
      return zipcode;
    }

    public void setZipcode(String zipcode) {
      this.zipcode = zipcode;
    }

    @Override
    public String toString() {
      return "Address{" +
              "address1='" + address1 + '\'' +
              ", address2='" + address2 + '\'' +
              ", city='" + city + '\'' +
              ", state='" + state + '\'' +
              ", zipcode='" + zipcode + '\'' +
              '}';
    }
  }
}
