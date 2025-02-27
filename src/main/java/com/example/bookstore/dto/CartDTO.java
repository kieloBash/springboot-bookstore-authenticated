package com.example.bookstore.dto;

import java.util.List;

public class CartDTO {
    private Integer id;
    private List<ItemCartDTO> items;
    private Double total_amount;
    private Integer user_id;

    public CartDTO(Integer id, List<ItemCartDTO> items, Double total_amount, Integer user_id) {
        this.id = id;
        this.items = items;
        this.total_amount = total_amount;
        this.user_id = user_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<ItemCartDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemCartDTO> items) {
        this.items = items;
    }

    public Double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Double total_amount) {
        this.total_amount = total_amount;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
}
