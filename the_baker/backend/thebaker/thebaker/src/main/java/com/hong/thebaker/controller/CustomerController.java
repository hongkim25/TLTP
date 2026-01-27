package com.hong.thebaker.controller;

import com.hong.thebaker.entity.Customer;
import com.hong.thebaker.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // Fixed: Return type is now <?> to allow returning a Map if customer is missing
    @GetMapping("/{phone}")
    public ResponseEntity<?> getCustomerPoints(@PathVariable String phone) {
        return customerRepository.findByPhone(phone)
                .map(customer -> ResponseEntity.ok((Object) customer)) // Found? Return Customer
                .orElse(ResponseEntity.ok(Map.of("points", 0)));       // New? Return {points: 0}
    }
}