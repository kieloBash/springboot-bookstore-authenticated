package com.example.bookstore.model;

import jakarta.persistence.*;
import lombok.*;


/**
 * Represents a book in the bookstore.
 *
 * @id Integer - Unique identifier for the book (Primary Key).
 * @name String - The name/title of the book.
 * @description String - A detailed description of the book.
 * @author String - The author of the book.
 * @category String - The category or genre of the book.
 * @price Double - The price of the book.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "price", nullable = false)
    private Double price;
}
