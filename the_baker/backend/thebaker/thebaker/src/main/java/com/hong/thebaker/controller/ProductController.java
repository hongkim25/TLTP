package com.hong.thebaker.controller;

import com.hong.thebaker.entity.Product;
import com.hong.thebaker.repository.ProductRepository;
import com.hong.thebaker.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Base64;
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

    // POST method for Staff to add items
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestParam("name") String name,
            @RequestParam("price") BigDecimal price,
            @RequestParam("category") String category,
            @RequestParam("stockQuantity") int stockQuantity,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) {
        try {
            Product product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setCategory(category);
            product.setStockQuantity(stockQuantity);

            // IMAGE LOGIC
            if (imageFile != null && !imageFile.isEmpty()) {
                String base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());
                product.setImageBase64("data:image/jpeg;base64," + base64Image); // Prefix for HTML
            }

            return ResponseEntity.ok(productRepository.save(product));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
    }

    @Autowired
    private PredictionService predictionService;
}