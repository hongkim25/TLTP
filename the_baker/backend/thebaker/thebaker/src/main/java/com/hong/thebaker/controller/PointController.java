package com.hong.thebaker.controller;

import com.hong.thebaker.entity.Customer;
import com.hong.thebaker.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
public class PointController {

    private final CustomerRepository customerRepo;

    public PointController(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    // POST /api/points/pay
    // Matches the logic in your staff.html processTransaction()
    @PostMapping("/pay")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> payload) {
        // 1. Extract Data
        String phone = (String) payload.get("phoneNumber");

        // Handle numbers safely (JSON sends numbers, Java needs BigDecimal)
        BigDecimal amount = new BigDecimal(payload.get("totalAmount").toString());
        String method = (String) payload.get("paymentMethod");

        // 2. Find or Create Customer
        Customer customer = customerRepo.findByPhone(phone)
                .orElseGet(() -> {
                    Customer newC = new Customer();
                    newC.setName("Guest " + (phone.length() > 4 ? phone.substring(phone.length()-4) : phone));
                    newC.setPhone(phone);
                    newC.setPoints(0);
                    return customerRepo.save(newC);
                });

        // 3. Calculate Points (3% Cash, 1% Card)
        double rate = "CASH".equals(method) ? 0.03 : 0.01;
        int pointsToAdd = amount.multiply(BigDecimal.valueOf(rate)).intValue();

        // 4. Save
        customer.setPoints(customer.getPoints() + pointsToAdd);
        customerRepo.save(customer);

        return ResponseEntity.ok("Points added: " + pointsToAdd);
    }
}