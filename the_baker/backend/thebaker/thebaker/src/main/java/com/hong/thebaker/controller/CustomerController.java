package com.hong.thebaker.controller;

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

    // Fixed: Use ResponseEntity<?> to allow returning either Customer OR a Map
    @GetMapping("/{phone}")
    public ResponseEntity<?> getCustomerPoints(@PathVariable String phone) {
        return customerRepository.findByPhone(phone)
                .map(customer -> ResponseEntity.ok((Object) customer)) // Case A: User Found
                .orElse(ResponseEntity.ok(Map.of("points", 0)));       // Case B: User Not Found
    }
}