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
                repository.save(new Product("사워도우", new BigDecimal("8500"), 10, "HARD"));
                repository.save(new Product("바게트", new BigDecimal("4500"), 20, "HARD"));

                // 2. SOFT BREADS (Sun, Mon, Wed) - Bagels
                repository.save(new Product("플레인 베이글", new BigDecimal("3500"), 30, "SOFT"));
                repository.save(new Product("블루베리 베이글", new BigDecimal("4000"), 30, "SOFT"));
                repository.save(new Product("소금빵", new BigDecimal("3800"), 20, "SOFT")); // Salt bread is usually soft!

                // 3. ALL DAYS (Coffee / Drinks / Basics)
                repository.save(new Product("아이스 아메리카노", new BigDecimal("4500"), 50, "ALL"));
                repository.save(new Product("아이스 라떼", new BigDecimal("5000"), 50, "ALL"));

                System.out.println("Database seeded with Day-Specific Menu!");
            }
        };
    }
}