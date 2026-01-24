package com.hong.thebaker.controller;

import com.hong.thebaker.service.PredictionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller // <--- Note: This is NOT RestController. It serves HTML.
public class StaffPageController {

    private final PredictionService predictionService;

    public StaffPageController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping("/staff") // This handles the URL 'thebaker.cc/staff'
    public String staffPage(
            // 1. LISTEN: Catch the manual weather inputs
            @RequestParam(value = "weather", defaultValue = "Sunny") String weather,
            @RequestParam(value = "temp", defaultValue = "20.0") double temp,
            Model model
    ) {
        // 2. DEFINE PRODUCTS
        List<String> products = List.of(
                "Bagel", "SaltBread", "Scone", "Sandwich", "Ciabatta", "Croissant"
                // Add other product names here exactly as they appear in history.csv
        );

        // 3. ASK AI: Get predictions based on the inputs
        List<PredictionService.PredictionResult> report = products.stream()
                .map(product -> predictionService.getPrediction(product, weather, temp))
                .collect(Collectors.toList());

        // 4. SEND TO HTML
        model.addAttribute("report", report);

        // Pass inputs back so the dropdown remembers what you picked
        model.addAttribute("currentWeather", weather);
        model.addAttribute("currentTemp", temp);

        return "staff"; // This loads staff.html
    }
}
