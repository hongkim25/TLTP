package com.hong.thebaker.controller;

import com.hong.thebaker.entity.Product;
import com.hong.thebaker.repository.ProductRepository;
import com.hong.thebaker.service.PredictionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller // <--- Note: This is NOT @RestController. It returns HTML.
public class SalesController {

    private final ProductRepository productRepo;
    private final PredictionService predictionService;

    // Inject the AI Service and Database
    public SalesController(ProductRepository productRepo, PredictionService predictionService) {
        this.productRepo = productRepo;
        this.predictionService = predictionService;
    }


    //EDIT!! 1. Show the Edit Form
    @GetMapping("/product/edit/{id}")
    public String editProductPage(@PathVariable Long id, Model model) {
        Product product = productRepo.findById(id).orElseThrow();
        model.addAttribute("product", product);
        return "edit_product"; // We will create this HTML next
    }

    // 2. Save the Changes
    @PostMapping("/product/update/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product formData) {
        Product existing = productRepo.findById(id).orElseThrow();

        // Update fields
        existing.setName(formData.getName());
        existing.setPrice(formData.getPrice());
        existing.setCategory(formData.getCategory());
        // We don't update image/stock here to keep it simple

        productRepo.save(existing);
        return "redirect:/staff"; // Go back to dashboard
    }
}