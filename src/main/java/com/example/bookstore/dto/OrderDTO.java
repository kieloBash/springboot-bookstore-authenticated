package com.example.bookstore.dto;

import java.util.Date;
import java.util.List;

public class OrderDTO {

    private Integer id;
    private Date orderDate;
    private Integer user_id;
    private Double total_amount;
    private List<ItemOrderDTO> items;

    public OrderDTO(Integer id, Date orderDate, Integer user_id, Double total_amount, List<ItemOrderDTO> items) {
        this.id = id;
        this.orderDate = orderDate;
        this.user_id = user_id;
        this.total_amount = total_amount;
        this.items = items;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Double total_amount) {
        this.total_amount = total_amount;
    }

    public List<ItemOrderDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemOrderDTO> items) {
        this.items = items;
    }
}
