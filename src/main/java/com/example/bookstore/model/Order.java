package com.example.bookstore.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents an order placed by a user in the bookstore.
 * An order can contain multiple items, and the total amount is calculated based on the price of the items.
 *
 * @id Integer - Unique identifier for the order (Primary Key).
 * @orderDate Date - The date when the order was placed.
 * @user User - The user associated with the order. This is a foreign key referencing the User entity.
 * @items List<ItemOrder> - A list of items in the order. Each item is represented by an ItemOrder object.
 * @total_amount Double - The total amount of all items in the order, calculated based on the price of each item.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date orderDate;

    @ManyToOne(fetch = FetchType.LAZY)  // Many carts can belong to one user (assuming Order is associated with OurUsers)
    @JoinColumn(name = "user_id")  // Foreign key column in Order referencing OurUsers
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)  // One Order can have many ItemOrders
    private List<ItemOrder> items;

    private Double total_amount;

    public Order(User user) {
        this.user = user;
        this.items = new ArrayList<>();
        this.total_amount = 0d;
        this.orderDate = new Date();
    }

    public void addItem(ItemOrder itemOrder){
        this.items.add(itemOrder);
    }

}
