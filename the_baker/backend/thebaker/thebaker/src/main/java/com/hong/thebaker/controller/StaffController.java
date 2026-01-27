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
import java.time.DayOfWeek; // <--- NEW
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    // --- DEPENDENCIES ---
    private final CustomerRepository customerRepo;
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;

    public StaffController(CustomerRepository customerRepo,
                           OrderRepository orderRepo,
                           ProductRepository productRepo) {
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    // --- CONFIGURATION ---
    private static final LocalTime OPEN_TIME = LocalTime.of(9, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(17, 0);

    // --- STATE (Manual Overrides) ---
    // These start as null/false, meaning "Follow the Schedule"
    private static boolean FORCE_OPEN = false;
    private static boolean FORCE_CLOSED = false;

    // --- METHOD 1: CHECK STATUS (Smart Logic) ---
    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        // 1. PRIORITY: Check Manual Overrides first
        if (FORCE_OPEN) return ResponseEntity.ok(Map.of("open", true));
        if (FORCE_CLOSED) return ResponseEntity.ok(Map.of("open", false));

        // 2. FALLBACK: Automatic Schedule
        boolean isOpen = checkSchedule();
        return ResponseEntity.ok(Map.of("open", isOpen));
    }

    // --- METHOD 2: TOGGLE STATUS (God Mode) ---
    @PostMapping("/status")
    public ResponseEntity<?> toggleStatus(@RequestBody Map<String, Boolean> payload) {
        boolean requestedOpen = payload.get("open");

        if (requestedOpen) {
            // User clicked "OPEN"
            FORCE_OPEN = true;
            FORCE_CLOSED = false; // Reset the other flag
        } else {
            // User clicked "CLOSE"
            FORCE_OPEN = false; // Reset the other flag
            FORCE_CLOSED = true;
        }

        return ResponseEntity.ok(Map.of(
                "message", "Manual Override: " + (requestedOpen ? "OPEN" : "CLOSED"),
                "open", requestedOpen
        ));
    }

    // --- HELPER: The Schedule Logic ---
    private boolean checkSchedule() {
        LocalDateTime now = LocalDateTime.now();
        LocalTime time = now.toLocalTime();
        DayOfWeek day = now.getDayOfWeek();

        // Rule 1: Closed on Tuesdays
        if (day == DayOfWeek.TUESDAY) {
            return false;
        }

        // Rule 2: Open between 08:00 and 20:00
        return time.isAfter(OPEN_TIME) && time.isBefore(CLOSE_TIME);
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

    // --- METHOD 4: UPDATE STOCK ---
    @PostMapping("/stock")
    public ResponseEntity<?> updateStock(@RequestBody Map<String, Integer> payload) {
        Long productId = Long.valueOf(payload.get("productId"));
        int newQuantity = payload.get("quantity");

        return productRepo.findById(productId)
                .map(product -> {
                    product.setStockQuantity(newQuantity);
                    productRepo.save(product);
                    return ResponseEntity.ok(Map.of("message", "Stock updated to " + newQuantity));
                })
                .orElse(ResponseEntity.badRequest().body(Map.of("error", "Product not found")));
    }
}