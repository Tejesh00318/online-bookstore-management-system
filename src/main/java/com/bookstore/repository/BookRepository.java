package com.bookstore.repository;

import com.bookstore.entity.Book;
import com.bookstore.entity.Book.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    List<Book> findByCategory(Category category);

    @Query("SELECT b FROM Book b WHERE b.stockQuantity > 0 ORDER BY b.createdAt DESC")
    List<Book> findAllInStock();

    @Query("SELECT b FROM Book b WHERE b.stockQuantity = 0")
    List<Book> findOutOfStockBooks();

    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.author LIKE %:keyword% OR b.isbn LIKE %:keyword%")
    List<Book> searchBooks(@Param("keyword") String keyword);

    @Query("SELECT b FROM Book b WHERE b.category = :category AND b.stockQuantity > 0")
    List<Book> findByCategoryAndInStock(@Param("category") Category category);

    @Query("SELECT b FROM Book b WHERE b.price BETWEEN :minPrice AND :maxPrice AND b.stockQuantity > 0")
    List<Book> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT COUNT(b) FROM Book b")
    Long countAllBooks();

    @Query("SELECT COUNT(b) FROM Book b WHERE b.stockQuantity > 0")
    Long countInStockBooks();

    @Query("SELECT COUNT(b) FROM Book b WHERE b.stockQuantity = 0")
    Long countOutOfStockBooks();

    @Query("SELECT b FROM Book b ORDER BY b.createdAt DESC LIMIT 10")
    List<Book> findRecentlyAddedBooks();

    @Query("SELECT DISTINCT b.category FROM Book b")
    List<Category> findAllCategories();
}