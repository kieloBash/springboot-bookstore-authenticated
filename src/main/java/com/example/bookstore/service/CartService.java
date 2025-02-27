package com.example.bookstore.service;

import com.example.bookstore.dto.BookDTO;
import com.example.bookstore.dto.CartDTO;
import com.example.bookstore.dto.ItemCartDTO;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Cart;
import com.example.bookstore.model.ItemCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CartRepository;
import com.example.bookstore.repository.ItemCartRepository;
import com.example.bookstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartsRepository;

    private final UserRepository usersRepository;

    private final BookRepository booksRepository;

    private final ItemCartRepository itemCartsRepository;

    public CartService(CartRepository cartsRepository, UserRepository usersRepository, BookRepository booksRepository, ItemCartRepository itemCartsRepository) {
        this.cartsRepository = cartsRepository;
        this.usersRepository = usersRepository;
        this.booksRepository = booksRepository;
        this.itemCartsRepository = itemCartsRepository;
    }

    public CartDTO getUserCart(String username){
        User user = this.usersRepository.findByUsername(username).orElseThrow(()->  new RuntimeException("User not found"));
        Integer user_id = Integer.parseInt(String.valueOf(user.getId()));

        Cart cart = this.cartsRepository
                .findByUserId(user_id)
                .orElseGet(()->new Cart(user));

        List<ItemCartDTO> itemCartDTOS = cart.getItems().stream().map(itemcart->new ItemCartDTO(
                itemcart.getId(),
                itemcart.getCart().getId(),
                new BookDTO(itemcart.getItem()),
                itemcart.getQuantity(),
                itemcart.getTotal_amount()
        )).toList();

        return new CartDTO(cart.getId(), itemCartDTOS, cart.getTotal_amount(),user_id);
    }

    @Transactional
    public boolean addBookToCart(String username, Integer book_id){
        User user = this.usersRepository.findByUsername(username).orElseThrow(()->  new RuntimeException("User not found"));
        Integer user_id = Integer.parseInt(String.valueOf(user.getId()));

        Cart cart = this.cartsRepository.findByUserId(user_id).orElseGet(()->new Cart(user));

        Book book = this.booksRepository.findById(book_id).orElseThrow(()-> new RuntimeException("Book not found"));

        Optional<ItemCart> itemInCart = this.itemCartsRepository.findByCart_IdAndItem_Id(cart.getId(),book.getId());

        if(itemInCart.isPresent()){
            ItemCart itemCart = itemInCart.get();
            itemCart.setQuantity(itemCart.getQuantity() + 1);
            itemCart.setTotal_amount(itemCart.getQuantity() * itemCart.getItem().getPrice());
        }else{
            ItemCart newItemCart = new ItemCart(book);
            newItemCart.setCart(cart);
            this.itemCartsRepository.save(newItemCart);
        }

        cart.setTotal_amount(cart.getItems().stream()
                .mapToDouble(ItemCart::getTotal_amount)
                .sum());

        this.cartsRepository.save(cart);
        return true;
    }


    @Transactional
    public boolean removeBookToCart(String username, Integer book_id) {
        User user = this.usersRepository.findByUsername(username).orElseThrow(()->  new RuntimeException("User not found"));
        Integer user_id = Integer.parseInt(String.valueOf(user.getId()));

        // Retrieve the user's cart
        Cart cart = this.cartsRepository.findByUserId(user_id)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // Find the item in the cart
        int itemIndex = -1;
        for (int i = 0; i < cart.getItems().size(); i++) {
            if (cart.getItems().get(i).getItem().getId() == book_id) {
                itemIndex = i;
                break;
            }
        }

        // If the item was not found in the cart
        if (itemIndex < 0) {
            throw new RuntimeException("Item Cart not found!");
        }


        // If the quantity is 1 or less, remove the item
        if (cart.getItems().get(itemIndex).getQuantity() - 1 > 0) {
            // Decrease the quantity of the item
            cart.getItems().get(itemIndex).setQuantity(cart.getItems().get(itemIndex).getQuantity() - 1);
            cart.getItems().get(itemIndex).setTotal_amount(cart.getItems().get(itemIndex).getQuantity() * cart.getItems().get(itemIndex).getItem().getPrice());

        } else {
            // Delete the item from the repository before removing from the list
            this.itemCartsRepository.delete(cart.getItems().get(itemIndex));

            // Remove the item from the cart's item list
            cart.getItems().remove(itemIndex);
        }

        cart.setTotal_amount(cart.getItems().stream()
                .mapToDouble(ItemCart::getTotal_amount)
                .sum());

        this.cartsRepository.save(cart);

        return true;
    }

    @Transactional
    public CartDTO clearCart(String username){
        User user = this.usersRepository.findByUsername(username).orElseThrow(()->  new RuntimeException("User not found"));
        Integer user_id = Integer.parseInt(String.valueOf(user.getId()));

        // Retrieve the user's cart
        Cart toDeleteCart = this.cartsRepository.findByUserId(user_id)
                .orElseThrow(() -> new RuntimeException("Cart not found!"));

        // Create ItemCartDTOs for the items in the cart before clearing
        List<ItemCartDTO> itemCartDTOS = toDeleteCart.getItems().stream()
                .map(itemcart -> new ItemCartDTO(
                        itemcart.getId(),
                        itemcart.getCart().getId(),
                        new BookDTO(itemcart.getItem()),
                        itemcart.getQuantity(),
                        itemcart.getTotal_amount()
                ))
                .toList();

        // Delete all item carts associated with this cart from the repository
        this.itemCartsRepository.deleteAll(toDeleteCart.getItems());

        // Clear the list of items from the cart (redundant after deleting them, but it's a good practice)
        toDeleteCart.setItems(new ArrayList<>());

        // Set the total amount to 0 after clearing the cart
        toDeleteCart.setTotal_amount(0.0);

        // Save the updated cart (with no items and total amount 0)
        this.cartsRepository.save(toDeleteCart);

        // Return the updated CartDTO
        return new CartDTO(toDeleteCart.getId(), new ArrayList<>(), toDeleteCart.getTotal_amount(), user_id);
    }
}
