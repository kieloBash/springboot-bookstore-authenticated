package com.example.bookstore.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a shopping cart associated with a user in the bookstore.
 * A cart can hold multiple items, and the total amount is calculated based on the items added.
 *
 * @id Integer - Unique identifier for the cart (Primary Key).
 * @user User - The user associated with the cart. This is a foreign key referencing the User entity.
 * @items List<ItemCart> - A list of items in the cart. Each item is represented by an ItemCart object.
 * @total_amount Double - The total amount of all items in the cart, calculated based on the price of each item.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)  // Many carts can belong to one user (assuming Cart is associated with OurUsers)
    @JoinColumn(name = "user_id")  // Foreign key column in Cart referencing OurUsers
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)  // One Cart can have many ItemCarts
    private List<ItemCart> items;

    private Double total_amount;

    public Cart(User user) {
        this.user = user;
        this.items = new ArrayList<>();
        this.total_amount = 0d;
    }

    public void addItem(ItemCart ic){
        this.items.add(ic);
    }

}
