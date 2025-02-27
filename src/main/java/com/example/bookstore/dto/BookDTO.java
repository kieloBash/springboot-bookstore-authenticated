package com.example.bookstore.dto;

import com.example.bookstore.model.Book;

public class BookDTO {
    private Integer id;
    private String name;
    private String description;
    private String author;
    private String category;
    private Double price;

    public BookDTO(){}

    public BookDTO(Book b){
        this.id = b.getId();
        this.name = b.getName();
        this.description = b.getDescription();
        this.author = b.getAuthor();
        this.category = b.getCategory();
        this.price = b.getPrice();
    }

    // Constructor to initialize the DTO from the Book entity
    public BookDTO(Integer id, String name, String description, String author, String category, Double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.author = author;
        this.category = category;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
