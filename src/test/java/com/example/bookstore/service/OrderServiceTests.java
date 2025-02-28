package com.example.bookstore.service;

import com.example.bookstore.dto.CartDTO;
import com.example.bookstore.dto.OrderDTO;
import com.example.bookstore.model.*;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CartRepository;
import com.example.bookstore.repository.OrderRepository;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTests {

    @Mock
    private OrderRepository ordersRepository;

    @Mock
    private UserRepository usersRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BookRepository booksRepository;

    @InjectMocks
    private OrderService orderService;

    private User mockUser;
    private Cart mockCart;
    private Book mockBook;
    private ItemOrder mockItemOrder;
    private Order mockOrder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Setup mock user
        mockUser = new User(1L, "testuser", "password", "test@example.com", "USER");

        // Setup mock book
        mockBook = new Book(1, "Book Title", "Description", "Author", "Category", 100.0);

        // Setup mock cart
        mockCart = new Cart(mockUser);
        mockCart.setId(1);
        mockCart.setTotal_amount(0d);

        // Setup mock order
        mockOrder = new Order(mockUser);
        mockOrder.setId(1);
        mockOrder.setTotal_amount(0d);


        // Setup mock itemcart
        ItemCart mockItemCart = new ItemCart(1,mockBook,1,mockCart);
        mockCart.addItem(mockItemCart);
        mockCart.setTotal_amount(100d);


        // Setup mock ItemOrder
        mockItemOrder = new ItemOrder(1,mockBook, 1,mockOrder);
        mockOrder.addItem(mockItemOrder);
        mockOrder.setTotal_amount(100d);

    }

    @Test
    public void testCreateOrderSuccessfully() {
        // Given
        when(usersRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(mockUser));
        when(cartRepository.findById(1))
                .thenReturn(Optional.of(mockCart));
        when(booksRepository.findById(1))
                .thenReturn(Optional.of(mockBook));
        when(ordersRepository.saveAndFlush(any(Order.class)))
                .thenReturn(mockOrder);

        // When
        Order result = orderService.createOrder("testuser", 1);

        // Then
        assertNotNull(result, "Expected order to be created");
        assertEquals(100.0, result.getTotal_amount(), "Expected total amount to be 100.0");
        assertEquals(1, result.getItems().size(), "Expected order to have 1 item");

        // Verify interactions
        verify(usersRepository, times(1)).findByUsername("testuser");
        verify(cartRepository, times(1)).findById(1);
        verify(booksRepository, times(1)).findById(1);
        verify(ordersRepository, times(1)).saveAndFlush(any(Order.class));
    }

    @Test
    public void testGetAllUserOrder(){
        // Given
        when(usersRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(mockUser));
        when(ordersRepository.findAllByUser_IdOrderByOrderDateDesc(1))
                .thenReturn(Arrays.asList(mockOrder));

        // When
        List<OrderDTO> result = this.orderService.getAllUserOrder("testuser");

        // Then
        assertNotNull(result,"Expected to not be null");
        assertEquals(1,result.size(),"Expected to only have 1 item");

        // Verify
        verify(usersRepository, times(1)).findByUsername("testuser");
        verify(ordersRepository, times(1)).findAllByUser_IdOrderByOrderDateDesc(1);
    }
}

