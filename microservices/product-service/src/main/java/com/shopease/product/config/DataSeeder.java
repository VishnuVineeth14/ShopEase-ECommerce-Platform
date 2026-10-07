package com.shopease.product.config;

import com.shopease.product.model.Category;
import com.shopease.product.model.Product;
import com.shopease.product.repository.CategoryRepository;
import com.shopease.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            List<Category> categories = categoryRepository.saveAll(List.of(
                    Category.builder().name("Electronics").description("Gadgets and devices").imageUrl("https://images.unsplash.com/photo-1498049794561-7780e7231661").createdAt(LocalDateTime.now()).build(),
                    Category.builder().name("Clothing").description("Apparel and fashion").imageUrl("https://images.unsplash.com/photo-1512436991641-6745cdb1723f").createdAt(LocalDateTime.now()).build(),
                    Category.builder().name("Books").description("Physical and digital books").imageUrl("https://images.unsplash.com/photo-1495446815901-a7297e633e8d").createdAt(LocalDateTime.now()).build(),
                    Category.builder().name("Shoes").description("Footwear").imageUrl("https://images.unsplash.com/photo-1542291026-7eec264c27ff").createdAt(LocalDateTime.now()).build(),
                    Category.builder().name("Home & Kitchen").description("Home appliances and decor").imageUrl("https://images.unsplash.com/photo-1556911220-bff31c812dba").createdAt(LocalDateTime.now()).build(),
                    Category.builder().name("Beauty").description("Cosmetics and self-care").imageUrl("https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9").createdAt(LocalDateTime.now()).build()
            ));
            System.out.println("Product Service: Categories seeded");

            if (productRepository.count() == 0) {
                Category electronics = categories.stream().filter(c -> c.getName().equals("Electronics")).findFirst().orElse(null);
                Category shoes = categories.stream().filter(c -> c.getName().equals("Shoes")).findFirst().orElse(null);

                if (electronics != null && shoes != null) {
                    productRepository.saveAll(List.of(
                            Product.builder()
                                    .name("Smartphone X")
                                    .description("Latest model with advanced camera.")
                                    .price(999.99)
                                    .stock(50)
                                    .imageUrl("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9")
                                    .categoryId(electronics.getId())
                                    .categoryName(electronics.getName())
                                    .sellerId("seller-1")
                                    .sellerName("Demo Seller")
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
                                    .sellerId("seller-1")
                                    .sellerName("Demo Seller")
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
                                    .sellerId("seller-1")
                                    .sellerName("Demo Seller")
                                    .rating(4.2)
                                    .reviewCount(10)
                                    .createdAt(LocalDateTime.now())
                                    .updatedAt(LocalDateTime.now())
                                    .build()
                    ));
                    System.out.println("Product Service: Products seeded");
                }
            }
        }
    }
}
