package com.example.bookstore.service;

import com.example.bookstore.dto.BookDTO;
import com.example.bookstore.dto.CartDTO;
import com.example.bookstore.dto.ItemOrderDTO;
import com.example.bookstore.dto.OrderDTO;
import com.example.bookstore.model.Cart;
import com.example.bookstore.model.ItemOrder;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CartRepository;
import com.example.bookstore.repository.OrderRepository;
import com.example.bookstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository ordersRepository;
    private final UserRepository usersRepository;
    private final BookRepository booksRepository;
    private final CartRepository cartRepository;

    public OrderService(OrderRepository ordersRepository, UserRepository usersRepository, BookRepository booksRepository, CartRepository cartRepository){
        this.ordersRepository = ordersRepository;
        this.usersRepository = usersRepository;
        this.booksRepository = booksRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public Order createOrder(String username, Integer cart_id){
        Cart cart = this.cartRepository.findById(cart_id).orElseThrow(()->new RuntimeException("Cart not found"));
        User currrentUser = this.usersRepository.findByUsername(username).orElseThrow(()->new RuntimeException("User not found"));

        Order newOrder = new Order(currrentUser);
        List<ItemOrder> itemOrderList = cart.getItems().stream()
                .map(itemCartDTO -> {
                    ItemOrder newItemOrder = new ItemOrder(
                            this.booksRepository
                                    .findById(itemCartDTO.getItem().getId())
                                    .orElseThrow(()->new RuntimeException("Book does not exist!")),
                            itemCartDTO.getQuantity());
                    newItemOrder.setOrder(newOrder);
                    return newItemOrder;
                })
                .toList();

        newOrder.setItems(itemOrderList);
        newOrder.setTotal_amount(cart.getTotal_amount());

        this.ordersRepository.saveAndFlush(newOrder);

        return newOrder;
    }

    public List<OrderDTO> getAllUserOrder(String username){
        User currrentUser = this.usersRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("User not found!"));
        Integer user_id = Integer.parseInt(currrentUser.getId().toString());

        List<Order> orders = this.ordersRepository.findAllByUser_IdOrderByOrderDateDesc(user_id);

        return orders.stream().map(order->{
            return new OrderDTO(order.getId(),
                    order.getOrderDate(),
                    Integer.parseInt(order.getUser().getId().toString()),
                    order.getTotal_amount(),

                    order.getItems().stream().map(itemOrder ->
                            new ItemOrderDTO(itemOrder.getId()
                                    ,order.getId(),
                                    new BookDTO(itemOrder.getItem()),
                                    itemOrder.getQuantity(),
                                    itemOrder.getTotal_amount())
                    ).toList()
            );
        }).toList();
    }
}
