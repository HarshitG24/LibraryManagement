package com.example.distributedsystems.distributed.systems.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "book")
public class Book {
  @Id
  private Long isbn;
  private String name;
  @Column(length = 1000)
  private String description;
  private String image;
  private Integer inventory;
  private String authorName;

  public Book() {
  }

  public Book(Long isbn, String name, String description, String image, Integer inventory, String authorName) {
    this.isbn = isbn;
    this.name = name;
    this.description = description;
    this.image = image;
    this.inventory = inventory;
    this.authorName = authorName;

  }

  public Long getIsbn() {
    return isbn;
  }

  public void setIsbn(Long isbn) {
    this.isbn = isbn;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public Integer getInventory() {
    return inventory;
  }

  public void setInventory(Integer inventory) {
    this.inventory = inventory;
  }

  public String getAuthorName() {
    return authorName;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

  @Override
  public String toString() {
    return "Book{" +
            "isbn=" + isbn +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", image='" + image + '\'' +
            ", inventory=" + inventory +
            ", authorName='" + authorName + '\'' +
            '}';
  }
}
