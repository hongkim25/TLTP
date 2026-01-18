package com.hong.thebaker.config;

import com.hong.thebaker.entity.Product;
import com.hong.thebaker.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            // Check if DB is empty before adding (so we don't duplicate on restart)
            if (repository.count() == 0) {

                // 1. HARD BREADS (Thu, Fri, Sat)
                repository.save(new Product("Sourdough", new BigDecimal("8500"), 12, "HARD"));
                repository.save(new Product("Baguette", new BigDecimal("4500"), 20, "HARD"));

                // 2. SOFT BREADS (Sun, Mon, Wed) - Bagels
                repository.save(new Product("Plain Bagel", new BigDecimal("3500"), 30, "SOFT"));
                repository.save(new Product("Blueberry Bagel", new BigDecimal("4000"), 25, "SOFT"));
                repository.save(new Product("Salt Bread", new BigDecimal("3800"), 40, "SOFT")); // Salt bread is usually soft!

                // 3. ALL DAYS (Coffee / Drinks / Basics)
                repository.save(new Product("Americano", new BigDecimal("4500"), 999, "ALL"));
                repository.save(new Product("Olive Oil", new BigDecimal("1000"), 50, "ALL"));

                System.out.println("✅ Database seeded with Day-Specific Menu!");
            }
        };
    }
}