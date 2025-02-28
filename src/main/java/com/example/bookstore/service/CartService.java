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

    /**
     * Retrieves the cart of a user based on their username.
     * If no cart is found, a new cart is created for the user.
     *
     * @param username The username of the user whose cart is to be retrieved.
     * @return A CartDTO containing the user's cart details, including the list of items and the total amount.
     * @throws RuntimeException If the user cannot be found.
     */
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

    /**
     * Adds a book to the user's cart. If the book is already in the cart, the quantity is incremented.
     *
     * @param username The username of the user to whose cart the book is to be added.
     * @param book_id  The ID of the book to be added to the cart.
     * @return True if the book was successfully added or quantity updated; false otherwise.
     * @throws RuntimeException If the user or book cannot be found.
     */
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

    /**
     * Removes a book from the user's cart. If the quantity of the book is greater than 1, the quantity is decreased.
     * If the quantity is 1, the book is removed from the cart entirely.
     *
     * @param username The username of the user whose cart the book is to be removed from.
     * @param book_id  The ID of the book to be removed from the cart.
     * @return True if the book was successfully removed; false otherwise.
     * @throws RuntimeException If the user, book, or cart cannot be found.
     */
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

        ItemCart selectedItem = cart.getItems().get(itemIndex);

        // If the quantity is 1 or less, remove the item
        if (selectedItem.getQuantity() - 1 > 0) {
            // Decrease the quantity of the item
            cart.getItems().get(itemIndex).setQuantity(selectedItem.getQuantity() - 1);
            cart.getItems().get(itemIndex).setTotal_amount(selectedItem.getQuantity() * selectedItem.getItem().getPrice());

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

    /**
     * Clears all items from the user's cart, setting the total amount to 0.
     *
     * @param username The username of the user whose cart is to be cleared.
     * @return A CartDTO representing the cleared cart.
     * @throws RuntimeException If the user or cart cannot be found.
     */
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
