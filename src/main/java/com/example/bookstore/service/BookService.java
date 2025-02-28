package com.example.bookstore.service;

import com.example.bookstore.dto.BookDTO;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    /**
     * Retrieves a list of all books, optionally filtered by name and/or category.
     *
     * @param filterName    The name to filter books by (optional).
     * @param filterCategory The category to filter books by (optional).
     * @return A list of BookDTO objects containing book details.
     */
    public List<BookDTO> getAllBooks(String filterName, String filterCategory) {
        System.out.println("Service starts...");
        List<Book> booksList = this.bookRepository.findAll();

        if(filterName != null && !filterName.trim().isEmpty()){
            booksList = booksList.stream()
                    .filter(book-> book.getName().equalsIgnoreCase(filterName))
                    .toList();
        }

        if(filterCategory != null && !filterCategory.trim().isEmpty()){
            booksList = booksList.stream()
                    .filter(book-> book.getCategory().equalsIgnoreCase(filterCategory))
                    .toList();
        }

        System.out.println("Service ends...");

        return booksList.stream()
                .map(BookDTO::new)
                .collect(Collectors.toList());
    }


    /**
     * Retrieves a book by its ID.
     *
     * @param id The ID of the book to retrieve.
     * @return A BookDTO object containing book details, or null if the book is not found.
     */
    public BookDTO getBookById(Integer id){
        Optional<Book> existingBook = this.bookRepository.findById(id);

        if(existingBook.isPresent()){
            Book b = existingBook.get();
            return new BookDTO(b);
        }else {
            return null;
        }
    }

    /**
     * Retrieves a list of all unique book categories.
     *
     * @return A list of unique book categories.
     */
    public List<String> getAllBookCategories() {
        List<Book> booksList = this.bookRepository.findAll();

        // Use a Set to get unique categories
        Set<String> uniqueCategories = booksList.stream()
                .map(Book::getCategory)  // Extract categories
                .collect(Collectors.toSet()); // Collect into a Set

        // Convert Set back to a List if you prefer returning a List
        return new ArrayList<>(uniqueCategories);
    }

}
