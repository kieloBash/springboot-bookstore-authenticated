package com.example.bookstore.controller;

import com.example.bookstore.dto.BookDTO;
import com.example.bookstore.dto.CartDTO;
import com.example.bookstore.model.Cart;
import com.example.bookstore.security.JwtFilter;
import com.example.bookstore.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final CartService cartsService;

    public CartController(CartService cartsService){
        this.cartsService = cartsService;
    }

    /**
     * Retrieves the current user's cart details.
     *
     * @param principal The authenticated user's principal object containing their username.
     * @return A ResponseEntity containing the CartDTO if found, or a 404 if not.
     */
    @GetMapping
    public ResponseEntity<CartDTO> getCart(Principal principal) {
        String username = principal.getName();

        try {
            CartDTO cart = this.cartsService.getUserCart(username);

            if(cart == null){
                return ResponseEntity.notFound().build(); // 404 Not Found
            }
            return ResponseEntity.ok(cart);

        } catch (Exception e) {
            // In case of an error (e.g., database connection issue), return 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // 500 Internal Server Error
        }
    }

    /**
     * Adds a book to the user's cart.
     * If the book is already in the cart, its quantity is incremented.
     *
     * @param principal The authenticated user's principal object containing their username.
     * @param book_id   The ID of the book to be added to the cart.
     * @return A ResponseEntity indicating the result of the operation (e.g., 204 No Content on success).
     */
    @PostMapping("/add-item")
    public ResponseEntity<Void> addItemToCart(Principal principal,
                                              @RequestParam(name = "bookId") Integer book_id) {
        String username = principal.getName();

        try {
            // Attempt to add the book to the cart
            Boolean added = this.cartsService.addBookToCart(username, book_id);

            // Return 404 if the book was not added (this happens if something like the book not being found)
            if (!added) {
                logger.info("Book not added");
                return ResponseEntity.notFound().build(); // 404 Not Found
            }

            logger.info("Book added: {}", book_id);
            // Return 204 No Content if the book was successfully added or quantity was updated
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            // Handle user not found or book not found exceptions (404)
            if (e.getMessage().contains("User not found") || e.getMessage().contains("Book not found")) {
                logger.info("User or Book not found!");
                return ResponseEntity.notFound().build(); // 404 Not Found
            }

            // In case of an internal server error (e.g., database issues)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // 500 Internal Server Error
        } catch (Exception e) {
            // Catch all other unexpected exceptions
            logger.info("Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // 500 Internal Server Error
        }
    }

    /**
     * Removes a book from the user's cart. If the quantity of the book is greater than 1, it decreases.
     * If the quantity is 1, the book is removed from the cart.
     *
     * @param principal The authenticated user's principal object containing their username.
     * @param book_id   The ID of the book to be removed from the cart.
     * @return A ResponseEntity indicating the result of the operation (e.g., 204 No Content on success).
     */
    @PutMapping("/delete-item")
    public ResponseEntity<Void> removeItemToCart(Principal principal,
                                              @RequestParam(name = "bookId") Integer book_id) {
        String username = principal.getName();

        try {
            Boolean added = this.cartsService.removeBookToCart(username, book_id);

            if (!added) {
                logger.info("Book not removed");
                return ResponseEntity.notFound().build(); // 404 Not Found
            }

            logger.info("Book removed: {}", book_id);
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            // Handle user not found or book not found exceptions (404)
            if (e.getMessage().contains("User not found") || e.getMessage().contains("Book not found")) {
                logger.info("User or Book not found!");
                return ResponseEntity.notFound().build(); // 404 Not Found
            }

            // In case of an internal server error (e.g., database issues)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // 500 Internal Server Error
        } catch (Exception e) {
            // Catch all other unexpected exceptions
            logger.info("Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // 500 Internal Server Error
        }
    }

    /**
     * Clears all items from the user's cart.
     *
     * @param principal The authenticated user's principal object containing their username.
     * @return A ResponseEntity indicating the result of the operation (e.g., 204 No Content on success).
     */
    @PutMapping("/clear-cart")
    public ResponseEntity<CartDTO> clearCart(Principal principal){
        String username = principal.getName();

        try {
           CartDTO newCart = this.cartsService.clearCart(username);

            if (newCart == null || newCart.getItems().isEmpty()) {
                logger.info("Cart still has items");
                return ResponseEntity.notFound().build(); // 404 Not Found
            }

            logger.info("Cart cleared");
            return ResponseEntity.noContent().build();

        }  catch (Exception e) {
            // Catch all other unexpected exceptions
            logger.info("Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // 500 Internal Server Error
        }
    }

}
