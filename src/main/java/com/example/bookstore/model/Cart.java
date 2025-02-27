package com.example.bookstore.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

}
