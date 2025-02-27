package com.example.bookstore.model;

import jakarta.persistence.*;
import lombok.*;

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

}
