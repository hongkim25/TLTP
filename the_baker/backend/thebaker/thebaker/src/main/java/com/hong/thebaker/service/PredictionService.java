package com.hong.thebaker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    private List<SalesRecord> history = new ArrayList<>();
    private JsonNode seasonalityData;

    // 1. LOAD DATA (Raw CSV History)
    @PostConstruct
    public void loadData() {
        try {
            // Load CSV
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new ClassPathResource("history.csv").getInputStream(), StandardCharsets.UTF_8));

            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    // --- YOUR EXACT INDICES ---
                    String rawName = parts[1].trim().replace(" ", ""); // Name
                    int qty = Integer.parseInt(parts[2].trim());       // Quantity
                    String weather = parts[4].trim();                  // Weather
                    double temp = Double.parseDouble(parts[5].trim()); // Temp

                    history.add(new SalesRecord(weather, temp, rawName, qty));
                } catch (Exception e) {
                    System.err.println("⚠️ Skipping bad CSV row: " + line);
                }
            }
            System.out.println("✅ HISTORY LOADED: " + history.size() + " records.");

            // Load Seasonality (JSON)
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = new ClassPathResource("seasonality.json").getInputStream();
            seasonalityData = mapper.readTree(is);
            System.out.println("✅ INTELLIGENCE LOADED: Seasonality Factors Ready.");

        } catch (Exception e) {
            System.err.println("❌ Error loading data: " + e.getMessage());
        }
    }

    public PredictionResult getPrediction(String productName, String weatherCondition, double temp) {
        String target = productName.trim().replace(" ", "");
        String category = mapProductToCategory(target); // For JSON lookup

        // --- STEP 1: BASELINE (From CSV History) ---
        // Level 1: Perfect Match (Weather + Temp +/- 5)
        List<SalesRecord> matches = history.stream()
                .filter(r -> r.product.equalsIgnoreCase(target))
                .filter(r -> r.weather.equalsIgnoreCase(weatherCondition))
                .filter(r -> Math.abs(r.temp - temp) <= 5.0)
                .collect(Collectors.toList());

        // Level 2: Weather Match Only
        if (matches.isEmpty()) {
            matches = history.stream()
                    .filter(r -> r.product.equalsIgnoreCase(target))
                    .filter(r -> r.weather.equalsIgnoreCase(weatherCondition))
                    .collect(Collectors.toList());
        }

        // Level 3: Product Match Only (Panic Fallback)
        if (matches.isEmpty()) {
            matches = history.stream()
                    .filter(r -> r.product.equalsIgnoreCase(target))
                    .collect(Collectors.toList());
        }

        if (matches.isEmpty()) {
            return new PredictionResult(productName, 0, 0, 0, "데이터 부족");
        }

        double baseAvg = matches.stream().mapToInt(r -> r.quantity).average().orElse(0);

        // --- STEP 2: INTELLIGENCE UPGRADE (From JSON) ---
        double finalPrediction = baseAvg;
        String reason = "OK";

        // Get Tomorrow's Day Factor
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String dayName = tomorrow.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH); // e.g. "Saturday"

        if (seasonalityData != null && seasonalityData.has(category)) {
            JsonNode productNode = seasonalityData.get(category);
            if (productNode.get("factors").has(dayName)) {
                double factor = productNode.get("factors").get(dayName).asDouble();

                // Apply the Boost
                finalPrediction = baseAvg * factor;

                // Format reason for UI
                if (factor > 1.05) reason = String.format("Boosted x%.2f (%s)", factor, dayName);
                else if (factor < 0.95) reason = String.format("Reduced x%.2f (%s)", factor, dayName);
            }
        }

        int recommended = (int) Math.ceil(finalPrediction * 1.1); // +10% Safety

        return new PredictionResult(productName, baseAvg, recommended, matches.size(), reason);
    }

    // --- HELPER METHODS ---

    private String mapProductToCategory(String name) {
        name = name.toLowerCase();
        if (name.contains("bagel") || name.contains("베이글")) return "bagel";
        if (name.contains("salt") || name.contains("소금")) return "salt";
        if (name.contains("sand") || name.contains("샌드")) return "sandwich";
        return "bagel";
    }

    private static class SalesRecord {
        String weather;
        double temp;
        String product;
        int quantity;
        public SalesRecord(String w, double t, String p, int q) {
            this.weather = w; this.temp = t; this.product = p; this.quantity = q;
        }
    }

    public static class PredictionResult {
        public String productName;
        public double avgSales;
        public int recommended;
        public int dataPoints;
        public String status;

        public PredictionResult(String name, double avg, int rec, int points, String stat) {
            this.productName = name;
            this.avgSales = avg;
            this.recommended = rec;
            this.dataPoints = points;
            this.status = stat;
        }
    }
}