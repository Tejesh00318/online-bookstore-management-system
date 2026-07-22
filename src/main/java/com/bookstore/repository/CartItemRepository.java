package com.bookstore.repository;

import com.bookstore.entity.CartItem;
import com.bookstore.entity.User;
import com.bookstore.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(User user);

    Optional<CartItem> findByUserAndBook(User user, Book book);

    void deleteByUser(User user);

    void deleteByUserAndBook(User user, Book book);

    @Query("SELECT COUNT(c) FROM CartItem c WHERE c.user = :user")
    Long countByUser(@Param("user") User user);

    @Query("SELECT SUM(c.quantity * c.book.price) FROM CartItem c WHERE c.user = :user")
    BigDecimal getTotalAmountByUser(@Param("user") User user);

    @Query("SELECT SUM(c.quantity) FROM CartItem c WHERE c.user = :user")
    Integer getTotalQuantityByUser(@Param("user") User user);
}