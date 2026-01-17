package com.hong.thebaker.controller;

import com.hong.thebaker.dto.OrderRequest;
import com.hong.thebaker.entity.Order;
import com.hong.thebaker.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
// @CrossOrigin logic will be needed later for Frontend
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // POST http://localhost:8080/api/orders
    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequest request) {
        // The Controller is "dumb". It just passes the request to the Service.
        Order newOrder = orderService.createOrder(request);
        return ResponseEntity.ok(newOrder);
    }
}