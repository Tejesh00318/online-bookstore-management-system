package com.bookstore.controller;

import com.bookstore.entity.User;
import com.bookstore.entity.CartItem;
import com.bookstore.entity.Order;
import com.bookstore.service.UserService;
import com.bookstore.service.CartService;
import com.bookstore.service.OrderService;
import com.bookstore.service.BookService;
import com.bookstore.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private BookService bookService;

    @GetMapping("/dashboard")
    public String userDashboard(@AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal principal, 
                               Model model) {
        User user = principal.getUser();

        // Get user statistics
        List<Order> recentOrders = orderService.findOrdersByUser(user).stream().limit(5).toList();
        BigDecimal cartTotal = cartService.getCartTotal(user);
        Integer cartItemCount = cartService.getCartItemCount(user);

        model.addAttribute("user", user);
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("cartTotal", cartTotal);
        model.addAttribute("cartItemCount", cartItemCount);
        model.addAttribute("pageTitle", "My Dashboard");

        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String userProfile(@AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal principal, 
                             Model model) {
        User user = principal.getUser();
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "My Profile");
        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal principal,
                               @Valid @ModelAttribute("user") User updatedUser,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "My Profile");
            return "user/profile";
        }

        try {
            User currentUser = principal.getUser();
            updatedUser.setId(currentUser.getId());
            userService.updateUser(updatedUser);

            redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
            return "redirect:/user/profile";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("pageTitle", "My Profile");
            return "user/profile";
        }
    }

    // Cart Management
    @GetMapping("/cart")
    public String viewCart(@AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal principal, 
                          Model model) {
        User user = principal.getUser();
        List<CartItem> cartItems = cartService.getCartItems(user);
        BigDecimal cartTotal = cartService.getCartTotal(user);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);
        model.addAttribute("pageTitle", "My Cart");

        return "user/cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal principal,
                           @RequestParam Long bookId,
                           @RequestParam(defaultValue = "1") Integer quantity,
                           RedirectAttributes redirectAttributes) {
        try {
            User user = principal.getUser();
            cartService.addToCart(user, bookId, quantity);
            redirectAttributes.addFlashAttribute("message", "Book added to cart successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/books/" + bookId;
    }

    @PostMapping("/cart/update")
    public String updateCartItem(@AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal principal,
                                @RequestParam Long cartItemId,
                                @RequestParam Integer quantity,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = principal.getUser();
            cartService.updateCartItem(user, cartItemId, quantity);
            redirectAttributes.addFlashAttribute("message", "Cart updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/user/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal principal,
                                @RequestParam Long cartItemId,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = principal.getUser();
            cartService.removeFromCart(user, cartItemId);
            redirectAttributes.addFlashAttribute("message", "Item removed from cart!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/user/cart";
    }

    @PostMapping("/cart/clear")
    public String clearCart(@AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal principal,
                           RedirectAttributes redirectAttributes) {
        User user = principal.getUser();
        cartService.clearCart(user);
        redirectAttributes.addFlashAttribute("message", "Cart cleared successfully!");
        return "redirect:/user/cart";
    }

    // Order Management
    @GetMapping("/checkout")
    public String checkoutPage(@AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal principal,
                              Model model) {
        User user = principal.getUser();
        List<CartItem> cartItems = cartService.getCartItems(user);

        if (cartItems.isEmpty()) {
            return "redirect:/user/cart?error=empty";
        }

        BigDecimal cartTotal = cartService.getCartTotal(user);

        model.addAttribute("user", user);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);
        model.addAttribute("pageTitle", "Checkout");

        return "user/checkout";
    }

    @PostMapping("/place-order")
    public String placeOrder(@AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal principal,
                            @RequestParam String shippingAddress,
                            @RequestParam(defaultValue = "Cash on Delivery") String paymentMethod,
                            @RequestParam(required = false) String specialInstructions,
                            RedirectAttributes redirectAttributes) {
        try {
            User user = principal.getUser();
            Order order = orderService.createOrderFromCart(user, shippingAddress, paymentMethod, specialInstructions);

            redirectAttributes.addFlashAttribute("message", 
                "Order placed successfully! Order Number: " + order.getOrderNumber());
            return "redirect:/user/orders/" + order.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/checkout";
        }
    }

    @GetMapping("/orders")
    public String userOrders(@AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal principal,
                            Model model) {
        User user = principal.getUser();
        List<Order> orders = orderService.findOrdersByUser(user);

        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "My Orders");

        return "user/orders";
    }

    @GetMapping("/orders/{orderId}")
    public String orderDetails(@AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal principal,
                              @PathVariable Long orderId,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            User user = principal.getUser();
            Order order = orderService.findById(orderId).orElse(null);

            // Check if order exists and belongs to the user
            if (order == null || !order.getUser().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "Order not found!");
                return "redirect:/user/orders";
            }

            model.addAttribute("order", order);
            model.addAttribute("orderItems", orderService.getOrderItems(orderId));
            model.addAttribute("pageTitle", "Order #" + order.getOrderNumber());

            return "user/order-details";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading order details!");
            return "redirect:/user/orders";
        }
    }
}