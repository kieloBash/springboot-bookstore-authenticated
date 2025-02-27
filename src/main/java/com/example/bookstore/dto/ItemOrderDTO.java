package com.example.bookstore.dto;

public class ItemOrderDTO {
    private Integer id;
    private Integer order_id;
    private BookDTO item;
    private Integer quantity;
    private Double total_amount;

    public ItemOrderDTO(){}

    public ItemOrderDTO(Integer id, Integer order_id, BookDTO item, Integer quantity, Double total_amount) {
        this.id = id;
        this.order_id = order_id;
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

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
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
