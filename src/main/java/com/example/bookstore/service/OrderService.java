package com.example.bookstore.service;

import com.example.bookstore.dto.*;
import com.example.bookstore.model.*;
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

    /**
     * Finds the corresponding book from the item cart.
     *
     * @param itemCart The item cart that contains the book reference.
     * @return The Book entity found from the repository.
     * @throws RuntimeException If the book is not found in the repository.
     */
    private Book findBookByItemCartDTO(ItemCart itemCart){
        try{
            return this.booksRepository
                    .findById(itemCart.getItem().getId())
                    .orElseThrow(()->new RuntimeException("Book does not exist!"));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new order based on the contents of the user's cart.
     * This method transfers the items from the cart to an order and calculates the total amount.
     *
     * @param username The username of the user making the order.
     * @param cart_id  The ID of the cart from which the order is created.
     * @return The newly created order.
     * @throws RuntimeException If the cart or user cannot be found.
     */
    @Transactional
    public Order createOrder(String username, Integer cart_id){
        Cart cart = this.cartRepository.findById(cart_id).orElseThrow(()->new RuntimeException("Cart not found"));
        User currrentUser = this.usersRepository.findByUsername(username).orElseThrow(()->new RuntimeException("User not found"));

        Order newOrder = new Order(currrentUser);
        List<ItemOrder> itemOrderList = cart.getItems()
                .stream()
                .map(itemCart ->
                {

                    ItemOrder newItemOrder = new ItemOrder(
                            findBookByItemCartDTO(itemCart),
                            itemCart.getQuantity());
                    newItemOrder.setOrder(newOrder);
                    return newItemOrder;

                })
                .toList();

        newOrder.setItems(itemOrderList);
        newOrder.setTotal_amount(cart.getTotal_amount());

        this.ordersRepository.saveAndFlush(newOrder);

        return newOrder;
    }

    /**
     * Retrieves all orders associated with a given user.
     * Orders are returned in descending order based on the order date.
     *
     * @param username The username of the user whose orders are to be retrieved.
     * @return A list of OrderDTOs representing the user's orders.
     * @throws RuntimeException If the user cannot be found.
     */
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

                    order.getItems()
                            .stream()
                            .map(itemOrder ->
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
