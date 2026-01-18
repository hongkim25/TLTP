package com.hong.thebaker.controller;

import com.hong.thebaker.dto.QuickPaymentRequest;
import com.hong.thebaker.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points") // This matches the Frontend URL
public class PointController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/pay")
    public ResponseEntity<String> processQuickPayment(@RequestBody QuickPaymentRequest request) {
        try {
            orderService.processQuickPayment(request);
            return ResponseEntity.ok("Payment & Points processed successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
