package com.example.bookstore.service;

import com.example.bookstore.dto.BookDTO;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.UserRepository;
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

public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this);

        // Setup mock user
        mockUser = new User(1L, "testuser", "password", "test@example.com", "USER");
    }

    @Test
    public void testRegisterUser(){
        // Given
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class)))
                .thenReturn(mockUser);

        // When
        User result = this.userService.registerUser("testuser","test@example.com","123");

        System.out.println(result);
        System.out.println(result.getUsername());

        // Then
        assertNotNull(result, "Expected to not be null");
        assertEquals("testuser",result.getUsername(),"Expected to be 'testuser'");


        // Verify
        verify(userRepository, times(1))
                .findByUsername("testuser");
        verify(userRepository, times(1))
                .findByEmail("test@example.com");
    }
}
