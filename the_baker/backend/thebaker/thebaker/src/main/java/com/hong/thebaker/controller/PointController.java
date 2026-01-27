package com.hong.thebaker.controller;

import com.hong.thebaker.entity.Customer;
import com.hong.thebaker.entity.PaymentMethod; // <--- Import your Enum
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

    @PostMapping("/pay")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> payload) {
        // 1. EXTRACT DATA
        String phone = (String) payload.get("phoneNumber");
        BigDecimal amount = new BigDecimal(payload.get("totalAmount").toString());

        // Convert String "CARD" -> Enum PaymentMethod.CARD
        String methodStr = (String) payload.get("paymentMethod");
        PaymentMethod method = PaymentMethod.valueOf(methodStr);

        // Get points to use (Default 0)
        int pointsToUse = 0;
        if (payload.get("pointsToUse") != null) {
            pointsToUse = Integer.parseInt(payload.get("pointsToUse").toString());
        }

        // 2. FIND CUSTOMER
        Customer customer = customerRepo.findByPhone(phone)
                .orElseGet(() -> {
                    Customer newC = new Customer();
                    newC.setName("Guest " + (phone.length() > 4 ? phone.substring(phone.length()-4) : phone));
                    newC.setPhone(phone);
                    newC.setPoints(0);
                    return customerRepo.save(newC);
                });

        // 3. CHECK BALANCE (The Redemption Logic)
        if (pointsToUse > 0) {
            if (customer.getPoints() < pointsToUse) {
                return ResponseEntity.badRequest().body("포인트 부족! (보유: " + customer.getPoints() + "P)");
            }
        }

        // 4. CALCULATE EARNED POINTS (Using the Enum Logic!)
        int pointsToAdd = method.calculatePoints(amount);

        // 5. UPDATE FINAL BALANCE
        // Logic: (Current - Used) + New Earned
        int newBalance = customer.getPoints() - pointsToUse + pointsToAdd;

        customer.setPoints(newBalance);
        customerRepo.save(customer);

        return ResponseEntity.ok("사용: " + pointsToUse + " | 적립: " + pointsToAdd + " | 잔액: " + newBalance);
    }
}