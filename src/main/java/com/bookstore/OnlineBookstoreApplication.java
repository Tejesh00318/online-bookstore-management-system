package com.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineBookstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookstoreApplication.class, args);
        System.out.println("========================================");
        System.out.println("📚 Online Bookstore Management System Started!");
        System.out.println("🌐 Access URL: http://localhost:8080");
        System.out.println("👨‍💼 Admin Login: admin/admin123");
        System.out.println("👤 Test User Login: user/user123");
        System.out.println("🛠️  H2 Console: http://localhost:8080/h2-console");
        System.out.println("📊 JDBC URL: jdbc:h2:mem:bookstore");
        System.out.println("👤 Username: sa | Password: (empty)");
        System.out.println("========================================");
    }
}