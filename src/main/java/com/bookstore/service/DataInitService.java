package com.bookstore.service;

import com.bookstore.entity.Book;
import com.bookstore.entity.Book.Category;
import com.bookstore.entity.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitService implements CommandLineRunner {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize users if not exist
        if (userRepository.count() == 0) {
            initializeUsers();
        }

        // Initialize books if not exist
        if (bookRepository.count() == 0) {
            initializeBooks();
        }
    }

    private void initializeUsers() {
        // Create admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@bookstore.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRole(User.Role.ADMIN);
        admin.setPhone("1234567890");
        admin.setAddress("123 Admin Street, Admin City");
        userRepository.save(admin);

        // Create test user
        User user = new User();
        user.setUsername("user");
        user.setEmail("user@bookstore.com");
        user.setPassword(passwordEncoder.encode("user123"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(User.Role.USER);
        user.setPhone("0987654321");
        user.setAddress("456 User Street, User City");
        userRepository.save(user);

        System.out.println("Sample users created successfully!");
    }

    private void initializeBooks() {
        // Fiction Books
        createBook("9780061120084", "To Kill a Mockingbird", "Harper Lee", 
                  "A classic novel of modern American literature.", 
                  new BigDecimal("12.99"), 25, Category.FICTION, 
                  "HarperCollins", 1960, 376, "English", "https://via.placeholder.com/300x400/667eea/ffffff?text=To+Kill+a+Mockingbird");

        createBook("9780451524935", "1984", "George Orwell", 
                  "A dystopian social science fiction novel.", 
                  new BigDecimal("13.99"), 30, Category.FICTION, 
                  "Secker & Warburg", 1949, 328, "English", "https://via.placeholder.com/300x400/667eea/ffffff?text=1984");

        createBook("9780743273565", "The Great Gatsby", "F. Scott Fitzgerald", 
                  "The story of Jay Gatsby's unrequited love for Daisy Buchanan.", 
                  new BigDecimal("11.99"), 20, Category.FICTION, 
                  "Scribner", 1925, 180, "English", "https://via.placeholder.com/300x400/667eea/ffffff?text=The+Great+Gatsby");

        // Children's Books
        createBook("9780439708180", "Harry Potter and the Sorcerer's Stone", "J.K. Rowling", 
                  "The first book in the Harry Potter series.", 
                  new BigDecimal("8.99"), 50, Category.CHILDREN, 
                  "Scholastic", 1997, 309, "English", "https://via.placeholder.com/300x400/f093fb/ffffff?text=Harry+Potter");

        createBook("9780064400557", "Where the Wild Things Are", "Maurice Sendak", 
                  "A children's picture book about a young boy named Max.", 
                  new BigDecimal("7.99"), 15, Category.CHILDREN, 
                  "Harper & Row", 1963, 48, "English", "https://via.placeholder.com/300x400/f093fb/ffffff?text=Wild+Things");

        createBook("9780525578079", "The Cat in the Hat", "Dr. Seuss", 
                  "A beloved children's book about a mischievous cat.", 
                  new BigDecimal("6.99"), 35, Category.CHILDREN, 
                  "Random House", 1957, 61, "English", "https://via.placeholder.com/300x400/f093fb/ffffff?text=Cat+in+Hat");

        createBook("9780064430180", "Goodnight Moon", "Margaret Wise Brown", 
                  "A bedtime story about a bunny saying goodnight.", 
                  new BigDecimal("5.99"), 40, Category.CHILDREN, 
                  "Harper & Brothers", 1947, 32, "English", "https://via.placeholder.com/300x400/f093fb/ffffff?text=Goodnight+Moon");

        // Science Fiction
        createBook("9780441172719", "Dune", "Frank Herbert", 
                  "A science fiction novel set in the distant future.", 
                  new BigDecimal("15.99"), 18, Category.SCIENCE_FICTION, 
                  "Chilton Books", 1965, 688, "English", "https://via.placeholder.com/300x400/764ba2/ffffff?text=Dune");

        createBook("9780553293357", "Foundation", "Isaac Asimov", 
                  "The first novel in the Foundation series.", 
                  new BigDecimal("14.99"), 22, Category.SCIENCE_FICTION, 
                  "Gnome Press", 1951, 244, "English", "https://via.placeholder.com/300x400/764ba2/ffffff?text=Foundation");

        // Fantasy
        createBook("9780547928227", "The Hobbit", "J.R.R. Tolkien", 
                  "A fantasy novel about Bilbo Baggins' adventure.", 
                  new BigDecimal("12.99"), 28, Category.FANTASY, 
                  "George Allen & Unwin", 1937, 310, "English", "https://via.placeholder.com/300x400/4facfe/ffffff?text=The+Hobbit");

        createBook("9780345339683", "The Lion, the Witch and the Wardrobe", "C.S. Lewis", 
                  "The first published book in The Chronicles of Narnia series.", 
                  new BigDecimal("10.99"), 32, Category.FANTASY, 
                  "Geoffrey Bles", 1950, 208, "English", "https://via.placeholder.com/300x400/4facfe/ffffff?text=Narnia");

        // Mystery
        createBook("9780062073488", "And Then There Were None", "Agatha Christie", 
                  "A mystery novel about ten strangers on an island.", 
                  new BigDecimal("11.99"), 24, Category.MYSTERY, 
                  "Collins Crime Club", 1939, 272, "English", "https://via.placeholder.com/300x400/ffc107/ffffff?text=And+Then+None");

        createBook("9780307387691", "The Girl with the Dragon Tattoo", "Stieg Larsson", 
                  "A psychological thriller and mystery novel.", 
                  new BigDecimal("13.99"), 19, Category.MYSTERY, 
                  "Norstedts Förlag", 2005, 590, "English", "https://via.placeholder.com/300x400/ffc107/ffffff?text=Dragon+Tattoo");

        // Romance
        createBook("9780140430004", "Pride and Prejudice", "Jane Austen", 
                  "A romantic novel about Elizabeth Bennet and Mr. Darcy.", 
                  new BigDecimal("10.99"), 26, Category.ROMANCE, 
                  "T. Egerton", 1813, 432, "English", "https://via.placeholder.com/300x400/dc3545/ffffff?text=Pride+Prejudice");

        createBook("9780380730407", "The Notebook", "Nicholas Sparks", 
                  "A romantic novel about a young couple's love story.", 
                  new BigDecimal("12.99"), 21, Category.ROMANCE, 
                  "Warner Books", 1996, 214, "English", "https://via.placeholder.com/300x400/dc3545/ffffff?text=The+Notebook");

        // Technology
        createBook("9780132350884", "Clean Code", "Robert C. Martin", 
                  "A handbook of agile software craftsmanship.", 
                  new BigDecimal("42.99"), 12, Category.TECHNOLOGY, 
                  "Prentice Hall", 2008, 464, "English", "https://via.placeholder.com/300x400/198754/ffffff?text=Clean+Code");

        createBook("9780321125217", "Domain-Driven Design", "Eric Evans", 
                  "Tackling complexity in the heart of software.", 
                  new BigDecimal("54.99"), 8, Category.TECHNOLOGY, 
                  "Addison-Wesley", 2003, 560, "English", "https://via.placeholder.com/300x400/198754/ffffff?text=DDD");

        // Business
        createBook("9780307887894", "The Lean Startup", "Eric Ries", 
                  "How today's entrepreneurs use continuous innovation.", 
                  new BigDecimal("18.99"), 14, Category.BUSINESS, 
                  "Crown Business", 2011, 336, "English", "https://via.placeholder.com/300x400/fd7e14/ffffff?text=Lean+Startup");

        createBook("9781591846444", "Good to Great", "Jim Collins", 
                  "Why some companies make the leap and others don't.", 
                  new BigDecimal("19.99"), 11, Category.BUSINESS, 
                  "HarperBusiness", 2001, 320, "English", "https://via.placeholder.com/300x400/fd7e14/ffffff?text=Good+to+Great");

        // Self Help
        createBook("9781982134136", "Atomic Habits", "James Clear", 
                  "An easy and proven way to build good habits.", 
                  new BigDecimal("17.99"), 23, Category.SELF_HELP, 
                  "Avery", 2018, 320, "English", "https://via.placeholder.com/300x400/0dcaf0/ffffff?text=Atomic+Habits");

        createBook("9781501144318", "The 7 Habits of Highly Effective People", "Stephen R. Covey", 
                  "Powerful lessons in personal change.", 
                  new BigDecimal("16.99"), 16, Category.SELF_HELP, 
                  "Free Press", 1989, 372, "English", "https://via.placeholder.com/300x400/0dcaf0/ffffff?text=7+Habits");

        System.out.println("Sample books created successfully!");
    }

    private void createBook(String isbn, String title, String author, String description, 
                           BigDecimal price, Integer stockQuantity, Category category,
                           String publisher, Integer publicationYear, Integer pages, 
                           String language, String imageUrl) {
        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription(description);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        book.setCategory(category);
        book.setPublisher(publisher);
        book.setPublicationYear(publicationYear);
        book.setPages(pages);
        book.setLanguage(language);
        book.setImageUrl(imageUrl);

        bookRepository.save(book);
    }
}