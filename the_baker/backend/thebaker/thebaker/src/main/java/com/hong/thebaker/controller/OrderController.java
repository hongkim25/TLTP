package com.hong.thebaker.controller;

import com.hong.thebaker.dto.OrderRequest;
import com.hong.thebaker.entity.Order;
import com.hong.thebaker.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
// @CrossOrigin logic will be needed later for Frontend
public class OrderController {

    @Autowired
    private OrderService orderService;

    // POST /api/orders
    // Receives JSON: { "phoneNumber": "...", "customerName": "...", "items": [...] }
    @PostMapping
    public Order createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    // GET /api/orders (For the Staff Page)
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders(); // We need to ensure Service has this method
    }
}