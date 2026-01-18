package com.hong.thebaker.config;

import com.hong.thebaker.entity.Customer;
import com.hong.thebaker.entity.Product;
import com.hong.thebaker.repository.CustomerRepository;
import com.hong.thebaker.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration // This tells Spring: "Read this file at startup"
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepo, CustomerRepository customerRepo) {
        return args -> {
            // 1. Create Fake Products (The Menu)
            if (productRepo.count() == 0) { // Only load if database is empty
                System.out.println("... Loading Initial Bakery Menu ...");

                Product p1 = new Product();
                p1.setName("사워도우");
                p1.setPrice(new BigDecimal("6500"));
                p1.setCategory("하드빵");
                p1.setAvailable(true);
                p1.setStockQuantity(10);
                p1.setDescription("Classic fermented sourdough with a crispy crust.");
                productRepo.save(p1);

                Product p2 = new Product();
                p2.setName("플레인 베이글");
                p2.setPrice(new BigDecimal("3500"));
                p2.setCategory("베이글");
                p2.setAvailable(true);
                p2.setStockQuantity(5);
                p2.setDescription("Zesty lemon cream layered between fluffy sponge.");
                productRepo.save(p2);

                Product p3 = new Product();
                p3.setName("아이스 아메리카노");
                p3.setPrice(new BigDecimal("4500"));
                p3.setCategory("음료");
                p3.setAvailable(true);
                p3.setStockQuantity(100); // Drinks are virtual stock
                productRepo.save(p3);
            }

            // 2. Create a Fake Customer (To test Login)
            if (customerRepo.count() == 0) {
                System.out.println("... Loading Test Customer ...");

                Customer c1 = new Customer();
                c1.setName("Hong Test");
                c1.setPhone("01012345678"); // The "Login ID"
                c1.setPoints(1500); // Already has some points!
                customerRepo.save(c1);
            }
        };
    }
}