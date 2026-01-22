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
        // Was: return orderRepository.findAllByOrderByOrderDateDesc();
        return orderRepository.findByIsArchivedFalseOrderByOrderDateDesc();
    }

    // the Archive Endpoint
    @PutMapping("/{id}/archive")
    public ResponseEntity<Void> archiveOrder(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setArchived(true); // Hide it
                    orderRepository.save(order);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}