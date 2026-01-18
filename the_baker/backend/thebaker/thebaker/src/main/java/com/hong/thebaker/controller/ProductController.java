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
    public List<Product> getMenuByDate(@RequestParam(required = false) String date) {
        List<Product> allProducts = productRepository.findAll();

        // 1. Determine the Target Date
        LocalDate targetDate;
        if (date == null || date.isEmpty()) {
            targetDate = LocalDate.now(); // Default to Today
        } else {
            try {
                targetDate = LocalDate.parse(date); // Parse "2026-01-22"
            } catch (Exception e) {
                targetDate = LocalDate.now(); // Fallback if format is wrong
            }
        }

        // 2. What day of the week is that?
        DayOfWeek dayOfWeek = targetDate.getDayOfWeek();

        // 3. Filter Logic (Same as before, but using the specific day)
        return allProducts.stream()
                .filter(product -> isAvailableOnDay(product, dayOfWeek))
                .collect(Collectors.toList());
    }

    // Rename 'isAvailableToday' to 'isAvailableOnDay' for clarity
    private boolean isAvailableOnDay(Product product, DayOfWeek day) {
        String type = product.getCategory();

        if (type == null || type.equals("ALL")) return true;

        // HARD BREAD: Thu, Fri, Sat
        if (type.equals("HARD")) {
            return day == DayOfWeek.THURSDAY ||
                    day == DayOfWeek.FRIDAY ||
                    day == DayOfWeek.SATURDAY;
        }

        // BAGELS (SOFT): Sun, Mon, Wed
        if (type.equals("SOFT")) {
            return day == DayOfWeek.SUNDAY ||
                    day == DayOfWeek.MONDAY ||
                    day == DayOfWeek.WEDNESDAY;
        }

        return false;
    }

    // Keep the POST method for Staff to add items
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
    }
}

