package com.example.bookstore.service;

import com.example.bookstore.dto.BookDTO;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;


public class BookServiceTests {

    @Mock
    private BookRepository booksRepository;

    @InjectMocks
    private BookService bookService;

    private List<Book> mockBooks;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Setup mock book list
        mockBooks = new ArrayList<>();
        mockBooks.add(new Book(1,"Book 1",
                "this is a test",
                "author",
                "fantasy",
                120d));
        mockBooks.add(new Book(2,"Book 2",
                "this is a test",
                "author",
                "fantasy",
                200d));
        mockBooks.add(new Book(3,"Book 3",
                "this is a test",
                "author",
                "comedy",
                600d));
    }

    @Test
    public void testGetAllBooksNoFilters() {

        // Given
        when(booksRepository.findAll()).thenReturn(mockBooks);

        // When
        List<BookDTO> result = bookService.getAllBooks(null, null);

        // Then
        assertEquals(3, result.size(), "Expected 3 books in the result");
        verify(booksRepository, times(1)).findAll(); // Verify findAll() was called once
    }

    @Test
    public void testGetAllBooksFilterName() {

        // Given
        when(booksRepository.findAll()).thenReturn(mockBooks);
        String filterName = "Book 1";

        // When
        List<BookDTO> result = bookService.getAllBooks(filterName, null);

        // Then
        assertEquals(1, result.size(), "Expected 1 book in the result");
        assertEquals("Book 1", result.get(0).getName(), "Expected 'Book 1` as the result of the book name");
        verify(booksRepository, times(1)).findAll(); // Verify findAll() was called once
    }

    @Test
    public void testGetAllBooksCategoryName() {

        // Given
        when(booksRepository.findAll()).thenReturn(mockBooks);
        String filterCategory = "fantasy";

        // When
        List<BookDTO> result = bookService.getAllBooks(null, filterCategory);

        // Then
        assertEquals(2, result.size(), "Expected 2 books in the result");
        assertTrue(result.stream().allMatch(bookDTO -> filterCategory.equals(bookDTO.getCategory())),
                "All books should belong to category: " + filterCategory);
        verify(booksRepository, times(1)).findAll(); // Verify findAll() was called once
    }

    @Test
    public void testGetAllBooksNoMatchingFilters() {

        // Given
        when(booksRepository.findAll()).thenReturn(mockBooks);
        String filterName = "Book 4";
        String filterCategory = "horror";

        // When
        List<BookDTO> result = bookService.getAllBooks(filterName, filterCategory);

        // Then
        assertEquals(0, result.size(), "Expected 0 books in the result");
        verify(booksRepository, times(1)).findAll(); // Verify findAll() was called once
    }

    @Test
    public void testGetAllBookCategories(){

        //When
        when(booksRepository.findAll()).thenReturn(mockBooks);

        //Given
        List<String> result = bookService.getAllBookCategories();

        //Then
        assertEquals(2,result.size(),"Expected only 2 categories in the result");
        verify(booksRepository,times(1)).findAll();
    }

    @Test
    public void testGetBookByIdFound(){

        //When
        when(booksRepository.findById(1)).thenReturn(Optional.ofNullable(mockBooks.get(0)));

        //Given
        BookDTO result = bookService.getBookById(1);

        //Then
        assertNotNull(result,"Expected result to be not-null");
        assertEquals(1,result.getId(),"Expected book ID to be 1");
        assertEquals("Book 1", result.getName(), "Expected book NAME to be Book 1");
        verify(booksRepository,times(1)).findById(1);
    }

    @Test
    public void testGetBookByIdNotFound(){

        //When
        when(booksRepository.findById(1)).thenReturn(Optional.empty());

        //Given
        BookDTO result = bookService.getBookById(1);

        //Then
        assertNull(result,"Expected result to be null");
        verify(booksRepository,times(1)).findById(1);
    }

}

