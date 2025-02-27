package com.example.bookstore.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

}
