package com.hong.thebaker.controller;

import com.hong.thebaker.entity.Customer;
import com.hong.thebaker.entity.Order;
import com.hong.thebaker.entity.OrderStatus;
import com.hong.thebaker.repository.CustomerRepository;
import com.hong.thebaker.repository.OrderRepository;
import com.hong.thebaker.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    // --- DEPENDENCIES ---
    private final CustomerRepository customerRepo;
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo; // <--- NEW CONNECTION

    // --- CONSTRUCTOR ---
    public StaffController(CustomerRepository customerRepo,
                           OrderRepository orderRepo,
                           ProductRepository productRepo) {
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    // --- VARIABLES (Global Settings) ---
    public static boolean IS_STORE_OPEN = true;

    // --- METHOD 1: CHECK STATUS ---
    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        return ResponseEntity.ok(Map.of("open", IS_STORE_OPEN));
    }

    // --- METHOD 2: TOGGLE STATUS (God Switch) ---
    @PostMapping("/status")
    public ResponseEntity<?> toggleStatus(@RequestBody Map<String, Boolean> payload) {
        IS_STORE_OPEN = payload.get("open");
        return ResponseEntity.ok(Map.of("message", "Store is now " + (IS_STORE_OPEN ? "OPEN" : "CLOSED")));
    }

    // --- METHOD 3: ADD POINTS (Walk-In) ---
    @PostMapping("/points")
    public ResponseEntity<?> addPointsManually(@RequestBody Map<String, Object> payload) {
        String phone = (String) payload.get("phoneNumber");
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());

        Customer customer = customerRepo.findByPhone(phone)
                .orElseGet(() -> {
                    Customer newC = new Customer();
                    newC.setName("Walk-in " + phone);
                    newC.setPhone(phone);
                    newC.setPoints(0);
                    return customerRepo.save(newC);
                });

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.COMPLETED);
        order.setTotalAmount(amount);

        int pointsToAdd = amount.multiply(BigDecimal.valueOf(0.05)).intValue();
        order.setPointsEarned(pointsToAdd);

        customer.setPoints(customer.getPoints() + pointsToAdd);
        customerRepo.save(customer);
        orderRepo.save(order);

        return ResponseEntity.ok(Map.of(
                "message", "Points Added!",
                "currentPoints", customer.getPoints(),
                "added", pointsToAdd
        ));
    }

    // --- METHOD 4: UPDATE STOCK (The New Part) ---
    // Usage: The staff tablet sends { "productId": 1, "quantity": 50 }
    @PostMapping("/stock")
    public ResponseEntity<?> updateStock(@RequestBody Map<String, Integer> payload) {
        // 1. Get the ID and Quantity from the JSON
        Long productId = Long.valueOf(payload.get("productId"));
        int newQuantity = payload.get("quantity");

        // 2. Find the product -> Update it -> Save it
        return productRepo.findById(productId)
                .map(product -> {
                    product.setStockQuantity(newQuantity);
                    productRepo.save(product); // Write to DB
                    return ResponseEntity.ok(Map.of("message", "Stock updated to " + newQuantity));
                })
                .orElse(ResponseEntity.badRequest().body(Map.of("error", "Product not found")));
    }

}