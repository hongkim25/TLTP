package com.hong.thebaker.controller;

import com.hong.thebaker.entity.Customer;
import com.hong.thebaker.entity.Order;
import com.hong.thebaker.entity.OrderStatus;
import com.hong.thebaker.entity.PaymentMethod;
import com.hong.thebaker.repository.CustomerRepository;
import com.hong.thebaker.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
public class PointController {

    private final CustomerRepository customerRepo;
    private final OrderRepository orderRepo;

    public PointController(CustomerRepository customerRepo, OrderRepository orderRepo) {
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;
    }

    @PostMapping("/pay")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> payload) {
        // 1. EXTRACT DATA
        String phone = (String) payload.get("phoneNumber");
        BigDecimal totalAmount = new BigDecimal(payload.get("totalAmount").toString());
        String methodStr = (String) payload.get("paymentMethod");
        PaymentMethod method = PaymentMethod.valueOf(methodStr);

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

        // 3. CHECK BALANCE
        if (pointsToUse > 0 && customer.getPoints() < pointsToUse) {
            return ResponseEntity.badRequest().body("포인트 부족! (보유: " + customer.getPoints() + "P)");
        }

        // 4. *** THE FIXED LOGIC (Net Pay) ***
        // Calculate the actual money paid (Total - Points)
        BigDecimal pointsValue = BigDecimal.valueOf(pointsToUse);
        BigDecimal realPaidAmount = totalAmount.subtract(pointsValue);

        // Safety: If result is negative, set to 0 (Shouldn't happen with logic above, but safe)
        if (realPaidAmount.compareTo(BigDecimal.ZERO) < 0) {
            realPaidAmount = BigDecimal.ZERO;
        }

        // Calculate points ONLY on the Real Paid Amount
        int pointsToAdd = method.calculatePoints(realPaidAmount);

        // 5. UPDATE BALANCE
        int oldBalance = customer.getPoints();
        int newBalance = oldBalance - pointsToUse + pointsToAdd;
        customer.setPoints(newBalance);
        customerRepo.save(customer);

        // 6. SAVE ORDER (For SQL Visibility)
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(totalAmount);
        order.setPointsUsed(pointsToUse);
        order.setPointsEarned(pointsToAdd); 
        order.setStatus(OrderStatus.COMPLETED);

        orderRepo.save(order);

        return ResponseEntity.ok(String.format("사용: %dP | 적립: %dP | 잔액: %dP", pointsToUse, pointsToAdd, newBalance));
    }
}