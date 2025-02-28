package com.example.bookstore.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents an item in an order. Each item corresponds to a book purchased in the order.
 * The total amount for the item is calculated based on the quantity and the price of the book.
 *
 * @id Integer - Unique identifier for the item in the order (Primary Key).
 * @order Order - The order to which this item belongs. This is a foreign key referencing the Order entity.
 * @item Book - The book associated with this item in the order. This is a foreign key referencing the Book entity.
 * @quantity Integer - The quantity of the book in the order.
 * @total_amount Double - The total amount for this item, calculated by multiplying the book's price by the quantity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "itemorders")
public class ItemOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)  // Many ItemOrder can belong to one Order
    @JoinColumn(name = "order_id")  // Foreign key column in ItemOrder referencing Cart
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)  // Many ItemOrder can reference one Book
    @JoinColumn(name = "book_id")  // Foreign key column in ItemOrder referencing Book
    private Book item;

    private Integer quantity;
    private Double total_amount;

    public ItemOrder(Book book){
        this.item = book;
        this.quantity = 1;
        this.total_amount = 1 * book.getPrice();
    }

    public ItemOrder(Book book, Integer quantity){
        this.item = book;
        this.quantity = quantity;
        this.total_amount = quantity * book.getPrice();
    }

    public ItemOrder(Integer id, Book b, Integer quantity, Order order){
        this.id = id;
        this.item = b;
        this.quantity = quantity;
        this.order = order;
        this.total_amount = quantity * b.getPrice();
    }


}
