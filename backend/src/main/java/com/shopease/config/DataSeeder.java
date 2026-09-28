package com.shopease.config;

import com.shopease.dto.RegisterRequest;
import com.shopease.model.Category;
import com.shopease.model.Product;
import com.shopease.model.Role;
import com.shopease.model.User;
import com.shopease.repository.CategoryRepository;
import com.shopease.repository.ProductRepository;
import com.shopease.repository.UserRepository;
import com.shopease.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
        seedCategories();
        seedProducts();
    }

    private void seedUsers() {
        if (!userRepository.existsByEmail("admin@example.com")) {
            authService.register(RegisterRequest.builder()
                    .name("Admin User")
                    .email("admin@example.com")
                    .password("Admin@123")
                    .confirmPassword("Admin@123")
                    .phone("1234567890")
                    .role(Role.ADMIN.name())
                    .address("Admin HQ")
                    .build());
            System.out.println("Admin seeded");
        }

        if (!userRepository.existsByEmail("seller@example.com")) {
            authService.register(RegisterRequest.builder()
                    .name("Demo Seller")
                    .email("seller@example.com")
                    .password("Seller@123")
                    .confirmPassword("Seller@123")
                    .phone("9876543210")
                    .role(Role.SELLER.name())
                    .address("Seller Warehouse")
                    .build());
            System.out.println("Seller seeded");
        }

        if (!userRepository.existsByEmail("customer@example.com")) {
            authService.register(RegisterRequest.builder()
                    .name("Demo Customer")
                    .email("customer@example.com")
                    .password("Customer@123")
                    .confirmPassword("Customer@123")
                    .phone("5551234567")
                    .role(Role.CUSTOMER.name())
                    .address("123 Customer St")
                    .build());
            System.out.println("Customer seeded");
        }
    }

    private void seedCategories() {
        if (categoryRepository.count() == 0) {
            categoryRepository.saveAll(List.of(
                    Category.builder().name("Electronics").description("Gadgets and devices").imageUrl("https://images.unsplash.com/photo-1498049794561-7780e7231661").createdAt(LocalDateTime.now()).build(),
                    Category.builder().name("Clothing").description("Apparel and fashion").imageUrl("https://images.unsplash.com/photo-1512436991641-6745cdb1723f").createdAt(LocalDateTime.now()).build(),
                    Category.builder().name("Books").description("Physical and digital books").imageUrl("https://images.unsplash.com/photo-1495446815901-a7297e633e8d").createdAt(LocalDateTime.now()).build(),
                    Category.builder().name("Shoes").description("Footwear").imageUrl("https://images.unsplash.com/photo-1542291026-7eec264c27ff").createdAt(LocalDateTime.now()).build(),
                    Category.builder().name("Home & Kitchen").description("Home appliances and decor").imageUrl("https://images.unsplash.com/photo-1556911220-bff31c812dba").createdAt(LocalDateTime.now()).build(),
                    Category.builder().name("Beauty").description("Cosmetics and self-care").imageUrl("https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9").createdAt(LocalDateTime.now()).build()
            ));
            System.out.println("Categories seeded");
        }
    }

    private void seedProducts() {
        if (productRepository.count() == 0) {
            User seller = userRepository.findByEmail("seller@example.com").orElse(null);
            Category electronics = categoryRepository.findByNameIgnoreCase("Electronics").orElse(null);
            Category shoes = categoryRepository.findByNameIgnoreCase("Shoes").orElse(null);

            if (seller != null && electronics != null && shoes != null) {
                productRepository.saveAll(List.of(
                        Product.builder()
                                .name("Smartphone X")
                                .description("Latest model with advanced camera.")
                                .price(999.99)
                                .stock(50)
                                .imageUrl("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9")
                                .categoryId(electronics.getId())
                                .categoryName(electronics.getName())
                                .sellerId(seller.getId())
                                .sellerName(seller.getName())
                                .rating(4.5)
                                .reviewCount(2)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build(),
                        Product.builder()
                                .name("Wireless Headphones")
                                .description("Noise-cancelling over-ear headphones.")
                                .price(199.99)
                                .stock(100)
                                .imageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e")
                                .categoryId(electronics.getId())
                                .categoryName(electronics.getName())
                                .sellerId(seller.getId())
                                .sellerName(seller.getName())
                                .rating(4.8)
                                .reviewCount(5)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build(),
                        Product.builder()
                                .name("Running Sneakers")
                                .description("Comfortable and lightweight for daily runs.")
                                .price(89.99)
                                .stock(30)
                                .imageUrl("https://images.unsplash.com/photo-1542291026-7eec264c27ff")
                                .categoryId(shoes.getId())
                                .categoryName(shoes.getName())
                                .sellerId(seller.getId())
                                .sellerName(seller.getName())
                                .rating(4.2)
                                .reviewCount(10)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build()
                ));
                System.out.println("Products seeded");
            }
        }
    }
}
