package com.hong.thebaker.controller;

import com.hong.thebaker.entity.Product;
import com.hong.thebaker.repository.ProductRepository;
import com.hong.thebaker.service.PredictionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class StaffPageController {

    private final PredictionService predictionService;
    private final ProductRepository productRepo;

    // 2. Inject Repository in Constructor
    public StaffPageController(PredictionService predictionService, ProductRepository productRepo) {
        this.predictionService = predictionService;
        this.productRepo = productRepo;
    }

    @GetMapping("/staff")
    public String staffPage(
            @RequestParam(value = "weather", defaultValue = "Sunny") String weather,
            @RequestParam(value = "temp", defaultValue = "20.0") double temp,
            Model model
    ) {
        // 3. FETCH REAL PRODUCTS FROM DB
        List<Product> products = productRepo.findAll();

        // 4. PREDICT FOR EACH REAL PRODUCT
        List<PredictionService.PredictionResult> report = products.stream()
                .map(product -> predictionService.getPrediction(product.getName(), weather, temp))
                .collect(Collectors.toList());

        model.addAttribute("report", report);

        // Pass inputs back for the simulation form
        model.addAttribute("currentWeather", weather);
        model.addAttribute("currentTemp", temp);

        return "staff";
    }
}