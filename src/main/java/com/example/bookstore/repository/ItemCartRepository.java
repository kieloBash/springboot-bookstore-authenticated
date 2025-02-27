package com.example.bookstore.repository;

import com.example.bookstore.model.ItemCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemCartRepository extends JpaRepository<ItemCart,Integer> {
    @Query(value =
            "SELECT ic FROM ItemCart ic WHERE ic.cart.id = ?1 and ic.item.id = ?2"
    )
    Optional<ItemCart> findByCart_IdAndItem_Id(Integer cartId, Integer itemId);
}