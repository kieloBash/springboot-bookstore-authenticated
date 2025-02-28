package com.example.bookstore.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents an item in a shopping cart. Each item corresponds to a book added to the cart.
 * The total amount for the item is calculated based on the quantity and the price of the book.
 *
 * @id Integer - Unique identifier for the item in the cart (Primary Key).
 * @cart Cart - The shopping cart to which this item belongs. This is a foreign key referencing the Cart entity.
 * @item Book - The book associated with this item in the cart. This is a foreign key referencing the Book entity.
 * @quantity Integer - The quantity of the book in the cart.
 * @total_amount Double - The total amount for this item, calculated by multiplying the book's price by the quantity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "itemcarts")
public class ItemCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)  // Many ItemCart can belong to one Cart
    @JoinColumn(name = "cart_id")  // Foreign key column in ItemCart referencing Cart
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)  // Many ItemCart can reference one Book
    @JoinColumn(name = "book_id")  // Foreign key column in ItemCart referencing Book
    private Book item;

    private Integer quantity;
    private Double total_amount;

    public ItemCart(Book book){
        this.item = book;
        this.quantity = 1;
        this.total_amount = 1 * book.getPrice();
    }

    public ItemCart(Integer id, Book book, Integer quantity, Cart cart){
        this.id = id;
        this.item = book;
        this.cart = cart;
        this.quantity = quantity;
        this.total_amount = book.getPrice() * quantity;
    }

}
