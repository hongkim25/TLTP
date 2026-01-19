package com.hong.thebaker.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class PredictionService {

    // HARDCODED BASELINE (Replace this with CSV Reader later)
    // Map<ProductId, AverageSales>
    private static final Map<Long, Integer> BASELINE_SALES = Map.of(
            1L, 15, // Product ID 1 usually sells 15
            2L, 20  // Product ID 2 usually sells 20
    );

    private final String API_KEY = "YOUR_OPENWEATHER_API_KEY"; // Paste yours tomorrow
    private final String CITY = "Daejeon";

    public String getPrediction(Long productId) {
        int baseline = BASELINE_SALES.getOrDefault(productId, 10);
        double weatherFactor = getWeatherFactor();

        int predicted = (int) (baseline * weatherFactor);

        String weatherNote = (weatherFactor < 1.0) ? " (Rain Incoming 🌧️)" : " (Clear Sky ☀️)";
        return "Recommended: " + predicted + weatherNote;
    }

    private double getWeatherFactor() {
        try {
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&appid=" + API_KEY;
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // Parse Weather Condition
            java.util.List<Map<String, Object>> weatherList = (java.util.List) response.get("weather");
            String main = (String) weatherList.get(0).get("main"); // "Rain", "Clear", "Clouds"

            if ("Rain".equalsIgnoreCase(main) || "Drizzle".equalsIgnoreCase(main)) {
                return 0.85; // Sell 15% less if raining
            }
            return 1.1; // Sell 10% more if clear
        } catch (Exception e) {
            System.out.println("Weather API Failed: " + e.getMessage());
            return 1.0; // Default to normal if API fails
        }
    }
}