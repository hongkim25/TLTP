package com.hong.thebaker.controller;

import com.hong.thebaker.entity.Product;
import com.hong.thebaker.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // GET /api/products -> Returns ONLY what is sold TODAY
    @GetMapping
    public List<Product> getTodayMenu() {
        List<Product> allProducts = productRepository.findAll();

        // 1. What day is it?
        DayOfWeek today = LocalDate.now().getDayOfWeek();

        // 2. Filter Logic
        return allProducts.stream()
                .filter(product -> isAvailableToday(product, today))
                .collect(Collectors.toList());
    }

    // The Logic Engine
    private boolean isAvailableToday(Product product, DayOfWeek today) {
        String type = product.getCategory(); // HARD, SOFT, ALL

        if (type == null || type.equals("ALL")) return true; // Drinks are always sold

        // HARD BREAD: Thu, Fri, Sat
        if (type.equals("HARD")) {
            return today == DayOfWeek.THURSDAY ||
                    today == DayOfWeek.FRIDAY ||
                    today == DayOfWeek.SATURDAY;
        }

        // BAGELS (SOFT): Sun, Mon, Wed
        if (type.equals("SOFT")) {
            return today == DayOfWeek.SUNDAY ||
                    today == DayOfWeek.MONDAY ||
                    today == DayOfWeek.WEDNESDAY;
        }

        return false; // Closed on Tuesday or unknown category
    }

    // Keep the POST method for Staff to add items
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }
}