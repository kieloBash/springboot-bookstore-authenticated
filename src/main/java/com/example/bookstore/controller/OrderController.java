package com.example.bookstore.controller;

import com.example.bookstore.dto.CartDTO;
import com.example.bookstore.dto.OrderDTO;
import com.example.bookstore.model.Order;
import com.example.bookstore.security.JwtFilter;
import com.example.bookstore.service.CartService;
import com.example.bookstore.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final OrderService ordersService;
    private final CartService cartsService;

    public OrderController(OrderService ordersService, CartService cartsService){
        this.ordersService = ordersService;
        this.cartsService = cartsService;
    }

    /**
     * Creates a new order from a user's cart and clears the cart afterward.
     *
     * @param principal The currently authenticated user.
     * @param cart_id   The ID of the user's cart to create an order from.
     * @return A ResponseEntity containing the created Order or an error response.
     */
    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(Principal principal, @RequestBody Integer cart_id) {
        String username = principal.getName();

        logger.info("cart id {}",cart_id);

        try {
            // Create the order
            Order createdOrder = this.ordersService.createOrder(username,cart_id);
            logger.info("createdOrder {}",createdOrder);
            // If the order is created successfully
            if (createdOrder != null) {
                // Clear the cart after order creation
                this.cartsService.clearCart(username);
                return ResponseEntity.ok(createdOrder);
            } else {
                // In case order creation fails (shouldn't really reach here, as we already check for null)
                return ResponseEntity.badRequest().body(null);
            }

        } catch (RuntimeException ex) {
            // Handle specific exceptions for different errors (e.g., user not found, book does not exist)
            if (ex.getMessage().contains("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // User not found
            } else if (ex.getMessage().contains("Book does not exist")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Invalid book in cart
            } else {
                // Generic error for other runtime exceptions
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }


    /**
     * Retrieves a list of all orders for the currently authenticated user.
     *
     * @param principal The currently authenticated user.
     * @return A ResponseEntity containing the list of OrderDTOs or an error response.
     */
    @GetMapping("/user")
    public ResponseEntity<List<OrderDTO>> getAllUserOrders(Principal principal){
        String username = principal.getName();

        try{
            List<OrderDTO> orderDTOList = this.ordersService.getAllUserOrder(username);
            return ResponseEntity.ok(orderDTOList);

        } catch (RuntimeException ex) {
            if (ex.getMessage().contains("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // User not found
            } else if (ex.getMessage().contains("Book does not exist")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Invalid book in cart
            } else {
                // Generic error for other runtime exceptions
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }


    }

}
