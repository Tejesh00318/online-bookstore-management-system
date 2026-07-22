package com.bookstore.service;

import com.bookstore.entity.Book;
import com.bookstore.entity.Book.Category;
import com.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> findAllInStock() {
        return bookRepository.findAllInStock();
    }

    public List<Book> findOutOfStockBooks() {
        return bookRepository.findOutOfStockBooks();
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public List<Book> findByCategory(Category category) {
        return bookRepository.findByCategory(category);
    }

    public List<Book> findByCategoryAndInStock(Category category) {
        return bookRepository.findByCategoryAndInStock(category);
    }

    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchBooks(keyword);
    }

    public List<Book> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return bookRepository.findByPriceRange(minPrice, maxPrice);
    }

    public List<Book> findRecentlyAddedBooks() {
        return bookRepository.findRecentlyAddedBooks();
    }

    public Book saveBook(Book book) {
        book.setUpdatedAt(LocalDateTime.now());
        return bookRepository.save(book);
    }

    public Book addBook(Book book) {
        // Check if ISBN already exists
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new RuntimeException("Book with ISBN " + book.getIsbn() + " already exists!");
        }

        return saveBook(book);
    }

    public Book updateBook(Book book) {
        Book existingBook = bookRepository.findById(book.getId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Check if ISBN is being changed to one that already exists
        if (!existingBook.getIsbn().equals(book.getIsbn()) && 
            bookRepository.existsByIsbn(book.getIsbn())) {
            throw new RuntimeException("Book with ISBN " + book.getIsbn() + " already exists!");
        }

        // Update all fields
        existingBook.setIsbn(book.getIsbn());
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setDescription(book.getDescription());
        existingBook.setPrice(book.getPrice());
        existingBook.setStockQuantity(book.getStockQuantity());
        existingBook.setCategory(book.getCategory());
        existingBook.setPublisher(book.getPublisher());
        existingBook.setPublicationYear(book.getPublicationYear());
        existingBook.setPages(book.getPages());
        existingBook.setLanguage(book.getLanguage());
        existingBook.setImageUrl(book.getImageUrl());
        existingBook.setUpdatedAt(LocalDateTime.now());

        return bookRepository.save(existingBook);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public boolean existsByIsbn(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }

    public void updateStock(Long bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        book.setStockQuantity(book.getStockQuantity() + quantity);
        book.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book);
    }

    public void decreaseStock(Long bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for book: " + book.getTitle());
        }

        book.setStockQuantity(book.getStockQuantity() - quantity);
        book.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book);
    }

    public List<Category> getAllCategories() {
        return Arrays.asList(Category.values());
    }

    public Long countAllBooks() {
        return bookRepository.countAllBooks();
    }

    public Long countInStockBooks() {
        return bookRepository.countInStockBooks();
    }

    public Long countOutOfStockBooks() {
        return bookRepository.countOutOfStockBooks();
    }

    public List<Category> findAllUsedCategories() {
        return bookRepository.findAllCategories();
    }
}