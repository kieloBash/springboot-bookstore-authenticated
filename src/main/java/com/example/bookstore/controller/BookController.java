package com.example.bookstore.controller;

import com.example.bookstore.dto.BookDTO;
import com.example.bookstore.model.Book;
import com.example.bookstore.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks(
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "category", required = false, defaultValue = "") String category) {

        try {
            System.out.println("Controller starts...");
            List<BookDTO> books = this.bookService.getAllBooks(search, category);

            // If no books are found after filtering, return 404 Not Found
            if (books.isEmpty()) {
                return ResponseEntity.notFound().build(); // 404 Not Found
            }

            System.out.println("Controller ends...");

            // If books are found, return them with a 200 OK status
            return ResponseEntity.ok(books); // 200 OK

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            // If an error occurs during processing, return a 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // 500 Internal Server Error
        }
    }


    @GetMapping("/{book_id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Integer book_id) {
        try {
            // Attempt to get the book by ID
            BookDTO existingBook = this.bookService.getBookById(book_id);

            // If book not found, return 404 Not Found
            if (existingBook == null) {
                return ResponseEntity.notFound().build(); // 404 Not Found
            }

            // Return the book with 200 OK
            return ResponseEntity.ok(existingBook); // 200 OK

        } catch (Exception e) {
            // In case of an error (e.g., database connection issue), return 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // 500 Internal Server Error
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllBookCategories() {
        try {
            // Attempt to get the list of categories
            List<String> categories = this.bookService.getAllBookCategories();

            // If no categories are found, return 404 Not Found
            if (categories == null || categories.isEmpty()) {
                return ResponseEntity.notFound().build(); // 404 Not Found
            }

            // Return the categories with 200 OK
            return ResponseEntity.ok(categories); // 200 OK

        } catch (Exception e) {
            // In case of an error (e.g., database connection issue), return 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // 500 Internal Server Error
        }
    }



}
