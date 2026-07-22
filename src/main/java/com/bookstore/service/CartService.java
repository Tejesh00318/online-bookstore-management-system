package com.bookstore.service;

import com.bookstore.entity.CartItem;
import com.bookstore.entity.User;
import com.bookstore.entity.Book;
import com.bookstore.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private BookService bookService;

    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    public CartItem addToCart(User user, Long bookId, Integer quantity) {
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Check if book is in stock
        if (!book.isInStock()) {
            throw new RuntimeException("Book is out of stock");
        }

        // Check if requested quantity is available
        if (book.getStockQuantity() < quantity) {
            throw new RuntimeException("Only " + book.getStockQuantity() + " items available in stock");
        }

        // Check if item already exists in cart
        Optional<CartItem> existingCartItem = cartItemRepository.findByUserAndBook(user, book);

        if (existingCartItem.isPresent()) {
            // Update quantity
            CartItem cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;

            // Check if new quantity is available
            if (book.getStockQuantity() < newQuantity) {
                throw new RuntimeException("Only " + book.getStockQuantity() + " items available in stock");
            }

            cartItem.setQuantity(newQuantity);
            cartItem.setUpdatedAt(LocalDateTime.now());
            return cartItemRepository.save(cartItem);
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem(user, book, quantity);
            return cartItemRepository.save(cartItem);
        }
    }

    public CartItem updateCartItem(User user, Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Verify that the cart item belongs to the user
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to cart item");
        }

        // Check if quantity is available
        if (cartItem.getBook().getStockQuantity() < quantity) {
            throw new RuntimeException("Only " + cartItem.getBook().getStockQuantity() + " items available in stock");
        }

        cartItem.setQuantity(quantity);
        cartItem.setUpdatedAt(LocalDateTime.now());
        return cartItemRepository.save(cartItem);
    }

    public void removeFromCart(User user, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Verify that the cart item belongs to the user
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to cart item");
        }

        cartItemRepository.delete(cartItem);
    }

    public void removeBookFromCart(User user, Long bookId) {
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        cartItemRepository.deleteByUserAndBook(user, book);
    }

    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }

    public BigDecimal getCartTotal(User user) {
        BigDecimal total = cartItemRepository.getTotalAmountByUser(user);
        return total != null ? total : BigDecimal.ZERO;
    }

    public Integer getCartItemCount(User user) {
        Integer count = cartItemRepository.getTotalQuantityByUser(user);
        return count != null ? count : 0;
    }

    public Long getCartItemsCount(User user) {
        return cartItemRepository.countByUser(user);
    }

    public boolean isBookInCart(User user, Long bookId) {
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        return cartItemRepository.findByUserAndBook(user, book).isPresent();
    }

    public Optional<CartItem> getCartItem(User user, Long bookId) {
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        return cartItemRepository.findByUserAndBook(user, book);
    }
}