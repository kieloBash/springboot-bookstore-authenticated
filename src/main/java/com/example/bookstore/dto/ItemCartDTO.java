package com.example.bookstore.dto;

public class ItemCartDTO {
    private Integer id;
    private Integer cart_id;
    private BookDTO item;
    private Integer quantity;
    private Double total_amount;

    public ItemCartDTO(Integer id, Integer cart_id, BookDTO item, Integer quantity, Double total_amount) {
        this.id = id;
        this.cart_id = cart_id;
        this.item = item;
        this.quantity = quantity;
        this.total_amount = total_amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCart_id() {
        return cart_id;
    }

    public void setCart_id(Integer cart_id) {
        this.cart_id = cart_id;
    }

    public BookDTO getItem() {
        return item;
    }

    public void setItem(BookDTO item) {
        this.item = item;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Double total_amount) {
        this.total_amount = total_amount;
    }
}

