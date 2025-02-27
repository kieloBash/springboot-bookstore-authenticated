package com.example.bookstore.service;

import com.example.bookstore.dto.CartDTO;
import com.example.bookstore.dto.ItemCartDTO;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Cart;
import com.example.bookstore.model.ItemCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CartRepository;
import com.example.bookstore.repository.ItemCartRepository;
import com.example.bookstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartServiceTests {

    @Mock
    private CartRepository cartsRepository;

    @Mock
    private UserRepository usersRepository;

    @Mock
    private BookRepository booksRepository;

    @Mock
    private ItemCartRepository itemCartsRepository;

    @InjectMocks
    private CartService cartService;

    private User mockUser;
    private Book mockBook;
    private Cart mockCart;
    private ItemCart mockItemCart;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Setup mock user
        mockUser = new User(1L, "username", "password", "test@gmail.com", "USER");

        // Setup mock book
        mockBook = new Book(1, "Test Book", "Test Description", "Test Author", "Test Category", 10.0);

        // Setup mock cart and item
        mockCart = new Cart(mockUser);
        mockItemCart = new ItemCart(mockBook);
        mockItemCart.setQuantity(1);
        mockItemCart.setTotal_amount(mockBook.getPrice() * mockItemCart.getQuantity());

        // Ensure the ItemCart has the Cart object set properly
        mockItemCart.setCart(mockCart); // This is critical for preventing the NPE

        // Add the item to the cart
        mockCart.addItem(mockItemCart);
    }


    @Test
    public void testGetUserCart() {
        // Given
        when(usersRepository.findByUsername("username")).thenReturn(Optional.of(mockUser));
        Integer user_id = Integer.parseInt(mockUser.getId().toString());

        when(cartsRepository.findByUserId(user_id)).thenReturn(Optional.of(mockCart));

        // When
        CartDTO result = cartService.getUserCart("username");

        // Then
        assertNotNull(result, "CartDTO should not be null");
        assertEquals(1, result.getItems().size(), "Expected 1 item in the cart");
        assertEquals(mockBook.getName(), result.getItems().get(0).getItem().getName(), "Expected name of the book to be the same");
        verify(cartsRepository, times(1)).findByUserId(user_id);
    }

    @Test
    public void testAddBookToCart() {
        // Given
        when(usersRepository.findByUsername("username")).thenReturn(Optional.of(mockUser));
        when(booksRepository.findById(mockBook.getId())).thenReturn(Optional.of(mockBook));
        Integer user_id = Integer.parseInt(mockUser.getId().toString());
        when(cartsRepository.findByUserId(user_id)).thenReturn(Optional.of(mockCart));

        // When
        boolean result = cartService.addBookToCart("username", mockBook.getId());
        if(result){
            mockCart.getItems().get(0).setQuantity(mockCart.getItems().get(0).getQuantity() + 1);
        }

        // Then
        assertTrue(result, "Expected the result to be true");
        assertEquals(2, mockCart.getItems().get(0).getQuantity(), "Expected 2 items in the cart after adding another book");
        verify(itemCartsRepository, times(1)).save(any(ItemCart.class));
        verify(cartsRepository, times(1)).save(mockCart);
    }

    @Test
    public void testRemoveBookToCart() {
        // Given
        when(usersRepository.findByUsername("username")).thenReturn(Optional.of(mockUser));
        when(booksRepository.findById(mockBook.getId())).thenReturn(Optional.of(mockBook));
        Integer user_id = Integer.parseInt(mockUser.getId().toString());
        when(cartsRepository.findByUserId(user_id)).thenReturn(Optional.of(mockCart));

        // Get the actual item in the cart that will be removed
        ItemCart itemInCart = mockCart.getItems().get(0);

        // When: Remove the book from the cart
        boolean result = cartService.removeBookToCart("username", mockBook.getId());

        if (result) {
            mockCart.setItems(new ArrayList<>());
        }

        // Then: Check that the item was deleted from the repository
        assertTrue(result, "Expected the result to be true");
        assertEquals(0, mockCart.getItems().size(), "Expected no more items");

        // Ensure that the actual item in the cart is deleted
        verify(itemCartsRepository, times(1)).delete(itemInCart); // Use the actual item to be deleted
        verify(cartsRepository, times(1)).save(mockCart);
    }

    @Test
    public void testClearCart() {
        // Given
        when(usersRepository.findByUsername("username")).thenReturn(Optional.of(mockUser));
        when(booksRepository.findById(mockBook.getId())).thenReturn(Optional.of(mockBook));
        Integer user_id = Integer.parseInt(mockUser.getId().toString());
        when(cartsRepository.findByUserId(user_id)).thenReturn(Optional.of(mockCart));

        // When: Remove the book from the cart
        CartDTO result = cartService.clearCart("username");

        // Then: Check that the item was deleted from the repository
        assertTrue(result.getItems().isEmpty(), "Expected the result to be empty");

        // Ensure that the actual item in the cart is deleted
        verify(cartsRepository, times(1)).save(mockCart);
    }



}
