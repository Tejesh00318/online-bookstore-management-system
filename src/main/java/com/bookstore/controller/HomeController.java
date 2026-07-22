package com.bookstore.controller;

import com.bookstore.entity.Book;
import com.bookstore.entity.Book.Category;
import com.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private BookService bookService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<Book> featuredBooks = bookService.findRecentlyAddedBooks();
        List<Category> categories = bookService.getAllCategories();

        model.addAttribute("featuredBooks", featuredBooks);
        model.addAttribute("categories", categories);
        model.addAttribute("pageTitle", "Welcome to Online Bookstore");

        return "home";
    }

    @GetMapping("/books")
    public String allBooks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            Model model) {

        List<Book> books;

        // Apply filters
        if (search != null && !search.trim().isEmpty()) {
            books = bookService.searchBooks(search.trim());
            model.addAttribute("searchQuery", search);
        } else if (category != null && !category.isEmpty()) {
            try {
                Category bookCategory = Category.valueOf(category);
                books = bookService.findByCategoryAndInStock(bookCategory);
                model.addAttribute("selectedCategory", category);
            } catch (IllegalArgumentException e) {
                books = bookService.findAllInStock();
            }
        } else if (minPrice != null && maxPrice != null && 
                   !minPrice.isEmpty() && !maxPrice.isEmpty()) {
            try {
                BigDecimal min = new BigDecimal(minPrice);
                BigDecimal max = new BigDecimal(maxPrice);
                books = bookService.findByPriceRange(min, max);
                model.addAttribute("minPrice", minPrice);
                model.addAttribute("maxPrice", maxPrice);
            } catch (NumberFormatException e) {
                books = bookService.findAllInStock();
            }
        } else {
            books = bookService.findAllInStock();
        }

        List<Category> categories = bookService.getAllCategories();

        model.addAttribute("books", books);
        model.addAttribute("categories", categories);
        model.addAttribute("pageTitle", "All Books");

        return "books/list";
    }

    @GetMapping("/books/{id}")
    public String bookDetails(@PathVariable Long id, Model model) {
        Optional<Book> bookOpt = bookService.findById(id);

        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            List<Book> relatedBooks = bookService.findByCategoryAndInStock(book.getCategory())
                    .stream()
                    .filter(b -> !b.getId().equals(book.getId()))
                    .limit(4)
                    .toList();

            model.addAttribute("book", book);
            model.addAttribute("relatedBooks", relatedBooks);
            model.addAttribute("pageTitle", book.getTitle());

            return "books/details";
        } else {
            return "redirect:/books?error=notfound";
        }
    }

    @GetMapping("/category/{category}")
    public String booksByCategory(@PathVariable String category, Model model) {
        try {
            Category bookCategory = Category.valueOf(category);
            List<Book> books = bookService.findByCategoryAndInStock(bookCategory);
            List<Category> categories = bookService.getAllCategories();

            model.addAttribute("books", books);
            model.addAttribute("categories", categories);
            model.addAttribute("selectedCategory", category);
            model.addAttribute("categoryDisplayName", bookCategory.getDisplayNameWithEmoji());
            model.addAttribute("pageTitle", bookCategory.getDisplayName() + " Books");

            return "books/category";
        } catch (IllegalArgumentException e) {
            return "redirect:/books?error=invalidcategory";
        }
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "About Us");
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Contact Us");
        return "contact";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("pageTitle", "Access Denied");
        return "error/access-denied";
    }
}