package com.hong.thebaker.controller;

import com.hong.thebaker.entity.Customer;
import com.hong.thebaker.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    // GET /api/customers/{phone}
    // Used by index.html to show points to the customer
    @GetMapping("/{phone}")
    public ResponseEntity<?> getCustomerPoints(@PathVariable String phone) {
        Optional<Customer> customer = customerRepository.findByPhone(phone);

        if (customer.isPresent()) {
            // Return just the points, or the whole object
            return ResponseEntity.ok(customer.get());
        } else {
            // If new customer, return 0 points (don't error out)
            return ResponseEntity.ok().body("{\"points\": 0}");
        }
    }
}