package com.shopease.user.config;

import com.shopease.user.model.Role;
import com.shopease.user.model.User;
import com.shopease.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@example.com")) {
            userRepository.save(User.builder()
                    .name("Admin User")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .phone("1234567890")
                    .address("Admin HQ")
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
            System.out.println("User Service: Admin user seeded");
        }

        if (!userRepository.existsByEmail("seller@example.com")) {
            userRepository.save(User.builder()
                    .name("Demo Seller")
                    .email("seller@example.com")
                    .password(passwordEncoder.encode("Seller@123"))
                    .role(Role.SELLER)
                    .phone("9876543210")
                    .address("Seller Warehouse")
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
            System.out.println("User Service: Seller user seeded");
        }

        if (!userRepository.existsByEmail("customer@example.com")) {
            userRepository.save(User.builder()
                    .name("Demo Customer")
                    .email("customer@example.com")
                    .password(passwordEncoder.encode("Customer@123"))
                    .role(Role.CUSTOMER)
                    .phone("5551234567")
                    .address("123 Customer St")
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
            System.out.println("User Service: Customer user seeded");
        }
    }
}
