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
import java.time.LocalTime;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    // --- DEPENDENCIES ---
    private final CustomerRepository customerRepo;
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;

    // --- CONSTRUCTOR ---
    public StaffController(CustomerRepository customerRepo,
                           OrderRepository orderRepo,
                           ProductRepository productRepo) {
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    // --- CONFIGURATION (Hours) ---
    // You can change these numbers to match your real store hours
    private static final LocalTime OPEN_TIME = LocalTime.of(8, 0);   // 08:00 AM
    private static final LocalTime CLOSE_TIME = LocalTime.of(20, 0); // 08:00 PM

    // --- STATE ---
    // false = We have bread. true = We manually closed early (Sold Out).
    private static boolean IS_SOLD_OUT = false;

    // --- METHOD 1: CHECK STATUS (Smart Logic) ---
    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        LocalTime now = LocalTime.now();

        // 1. Is it operating hours?
        boolean isOperatingHours = now.isAfter(OPEN_TIME) && now.isBefore(CLOSE_TIME);

        // 2. The Final Verdict
        // Open ONLY if it's day time AND we are not sold out.
        boolean isOpen = isOperatingHours && !IS_SOLD_OUT;

        return ResponseEntity.ok(Map.of("open", isOpen));
    }

    // --- METHOD 2: TOGGLE STATUS (Sold Out Switch) ---
    @PostMapping("/status")
    public ResponseEntity<?> toggleStatus(@RequestBody Map<String, Boolean> payload) {
        LocalTime now = LocalTime.now();
        boolean isOperatingHours = now.isAfter(OPEN_TIME) && now.isBefore(CLOSE_TIME);

        // If it is Night Time, you cannot open the shop no matter what.
        if (!isOperatingHours) {
            return ResponseEntity.ok(Map.of("open", false));
        }

        // If it IS Day Time, the button toggles the "Sold Out" state.
        boolean requestedOpen = payload.get("open");

        // If user requests "Open", it means IS_SOLD_OUT should be false.
        IS_SOLD_OUT = !requestedOpen;

        return ResponseEntity.ok(Map.of("message", "Shop status updated", "open", requestedOpen));
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