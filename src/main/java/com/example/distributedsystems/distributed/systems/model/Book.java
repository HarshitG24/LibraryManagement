package com.example.distributedsystems.distributed.systems.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * model for storing the book data
 */
@Entity
@Table(name = "book")
public class Book {
  @Id
  private Long isbn;
  private String name;
  @Column(length = 3000)
  private String description;
  private String image;
  private Integer inventory;
  private String authorName;

  public Book() {
  }

  /**
   *
   * @param isbn of the book (unique id)
   * @param name of the book
   * @param description small description about the book
   * @param  image  image link for the book
   * @param inventory availability count for the book
   * @param authorName name of the author
   */
  public Book(Long isbn, String name, String description, String image, Integer inventory, String authorName) {
    this.isbn = isbn;
    this.name = name;
    this.description = description;
    this.image = image;
    this.inventory = inventory;
    this.authorName = authorName;

  }

  /**
   * getters and setters
   */

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
