package com.bookstore.controller;

import com.bookstore.entity.Book;
import com.bookstore.entity.Book.Category;
import com.bookstore.entity.User;
import com.bookstore.entity.Order;
import com.bookstore.service.BookService;
import com.bookstore.service.UserService;
import com.bookstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // Dashboard statistics
        Long totalBooks = bookService.countAllBooks();
        Long inStockBooks = bookService.countInStockBooks();
        Long outOfStockBooks = bookService.countOutOfStockBooks();
        Long totalUsers = userService.countCustomers();
        Long totalOrders = orderService.countAllOrders();
        Long pendingOrders = orderService.countOrdersByStatus(Order.OrderStatus.PENDING);
        BigDecimal totalRevenue = orderService.getTotalRevenue();
        BigDecimal todayRevenue = orderService.getTodayRevenue();
        BigDecimal monthRevenue = orderService.getThisMonthRevenue();
        Long todayOrders = orderService.getTodayOrdersCount();

        // Recent data
        List<Book> recentBooks = bookService.findRecentlyAddedBooks();
        List<Order> recentOrders = orderService.findAllOrders().stream().limit(10).toList();

        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("inStockBooks", inStockBooks);
        model.addAttribute("outOfStockBooks", outOfStockBooks);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("todayRevenue", todayRevenue);
        model.addAttribute("monthRevenue", monthRevenue);
        model.addAttribute("todayOrders", todayOrders);
        model.addAttribute("recentBooks", recentBooks);
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("pageTitle", "Admin Dashboard");

        return "admin/dashboard";
    }

    // Book Management
    @GetMapping("/books")
    public String manageBooks(@RequestParam(required = false) String search, Model model) {
        List<Book> books;

        if (search != null && !search.trim().isEmpty()) {
            books = bookService.searchBooks(search.trim());
            model.addAttribute("searchQuery", search);
        } else {
            books = bookService.findAllBooks();
        }

        model.addAttribute("books", books);
        model.addAttribute("pageTitle", "Manage Books");
        return "admin/books/list";
    }

    @GetMapping("/books/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", Arrays.asList(Category.values()));
        model.addAttribute("pageTitle", "Add New Book");
        return "admin/books/add";
    }

    @PostMapping("/books/add")
    public String addBook(@Valid @ModelAttribute("book") Book book,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", Arrays.asList(Category.values()));
            model.addAttribute("pageTitle", "Add New Book");
            return "admin/books/add";
        }

        try {
            bookService.addBook(book);
            redirectAttributes.addFlashAttribute("message", "Book added successfully!");
            return "redirect:/admin/books";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", Arrays.asList(Category.values()));
            model.addAttribute("pageTitle", "Add New Book");
            return "admin/books/add";
        }
    }

    @GetMapping("/books/{id}/edit")
    public String editBookForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Book book = bookService.findById(id).orElse(null);

        if (book == null) {
            redirectAttributes.addFlashAttribute("error", "Book not found!");
            return "redirect:/admin/books";
        }

        model.addAttribute("book", book);
        model.addAttribute("categories", Arrays.asList(Category.values()));
        model.addAttribute("pageTitle", "Edit Book");
        return "admin/books/edit";
    }

    @PostMapping("/books/{id}/edit")
    public String updateBook(@PathVariable Long id,
                           @Valid @ModelAttribute("book") Book book,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        book.setId(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", Arrays.asList(Category.values()));
            model.addAttribute("pageTitle", "Edit Book");
            return "admin/books/edit";
        }

        try {
            bookService.updateBook(book);
            redirectAttributes.addFlashAttribute("message", "Book updated successfully!");
            return "redirect:/admin/books";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", Arrays.asList(Category.values()));
            model.addAttribute("pageTitle", "Edit Book");
            return "admin/books/edit";
        }
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("message", "Book deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting book: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }

    // User Management
    @GetMapping("/users")
    public String manageUsers(@RequestParam(required = false) String search, Model model) {
        List<User> users;

        if (search != null && !search.trim().isEmpty()) {
            users = userService.searchUsers(search.trim());
            model.addAttribute("searchQuery", search);
        } else {
            users = userService.findAllCustomers();
        }

        model.addAttribute("users", users);
        model.addAttribute("pageTitle", "Manage Users");
        return "admin/users/list";
    }

    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.findById(id).orElse(null);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
            return "redirect:/admin/users";
        }

        List<Order> userOrders = orderService.findOrdersByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("userOrders", userOrders);
        model.addAttribute("pageTitle", "User Details - " + user.getFullName());
        return "admin/users/details";
    }

    // Order Management
    @GetMapping("/orders")
    public String manageOrders(@RequestParam(required = false) String status, Model model) {
        List<Order> orders;

        if (status != null && !status.isEmpty()) {
            try {
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
                orders = orderService.findByStatus(orderStatus);
                model.addAttribute("selectedStatus", status);
            } catch (IllegalArgumentException e) {
                orders = orderService.findAllOrders();
            }
        } else {
            orders = orderService.findAllOrders();
        }

        model.addAttribute("orders", orders);
        model.addAttribute("orderStatuses", Arrays.asList(Order.OrderStatus.values()));
        model.addAttribute("pageTitle", "Manage Orders");
        return "admin/orders/list";
    }

    @GetMapping("/orders/{id}")
    public String viewOrder(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Order order = orderService.findById(id).orElse(null);

        if (order == null) {
            redirectAttributes.addFlashAttribute("error", "Order not found!");
            return "redirect:/admin/orders";
        }

        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderService.getOrderItems(id));
        model.addAttribute("orderStatuses", Arrays.asList(Order.OrderStatus.values()));
        model.addAttribute("pageTitle", "Order Details - " + order.getOrderNumber());
        return "admin/orders/details";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                  @RequestParam String status,
                                  RedirectAttributes redirectAttributes) {
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
            orderService.updateOrderStatus(id, orderStatus);
            redirectAttributes.addFlashAttribute("message", "Order status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating order status: " + e.getMessage());
        }

        return "redirect:/admin/orders/" + id;
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.cancelOrder(id);
            redirectAttributes.addFlashAttribute("message", "Order cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error cancelling order: " + e.getMessage());
        }

        return "redirect:/admin/orders/" + id;
    }
}