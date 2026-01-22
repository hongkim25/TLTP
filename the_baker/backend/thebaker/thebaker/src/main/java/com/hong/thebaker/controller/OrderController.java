package com.hong.thebaker.controller;

import com.hong.thebaker.dto.OrderRequest;
import com.hong.thebaker.entity.Order;
import com.hong.thebaker.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 1. Create Order
    @PostMapping
    public Order createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    // 2. Get All (Active) Orders
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // 3. Archive Order (Delegates to Service)
    @PutMapping("/{id}/archive")
    public ResponseEntity<Void> archiveOrder(@PathVariable Long id) {
        orderService.archiveOrder(id);
        return ResponseEntity.ok().build();
    }

    // 4. Cancel Order (Delegates to Service)
    @PostMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        try {
            orderService.cancelOrder(id);
            return ResponseEntity.ok("Order cancelled and stock restored");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 5. Search My Orders (Delegates to Service)
    @GetMapping("/search")
    public List<Order> findMyOrders(@RequestParam String phone) {
        return orderService.findMyOrders(phone);
    }
}