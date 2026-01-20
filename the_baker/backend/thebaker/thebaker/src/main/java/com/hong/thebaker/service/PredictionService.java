package com.hong.thebaker.service;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    private List<SalesRecord> history = new ArrayList<>();

    // 1. LOAD DATA (Raw Korean Names)
    @PostConstruct
    public void loadData() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new ClassPathResource("history.csv").getInputStream(), StandardCharsets.UTF_8));

            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                try {
                    // Split by comma
                    String[] parts = line.split(",");

                    // Index 1: Product Name
                    String rawName = parts[1].trim().replace(" ", "");

                    // Index 2: Quantity
                    int qty = Integer.parseInt(parts[2].trim());

                    // Index 3 is Revenue (skipped)

                    // Index 4: Weather
                    String weather = parts[4].trim();

                    // Index 5: Temp
                    double temp = Double.parseDouble(parts[5].trim());

                    // DEBUG: See the clean data
                    System.out.println("✅ Loaded: " + rawName + " | Qty: " + qty + " | W: " + weather);

                    history.add(new SalesRecord(weather, temp, rawName, qty));
                } catch (Exception e) {
                    // Helpful error log to find bad rows
                    System.err.println("⚠️ Skipping bad row: " + line);
                }
            }
            System.out.println("✅ AI ENGINE: Loaded " + history.size() + " records (Korean Exact Match).");
        } catch (Exception e) {
            System.err.println("❌ AI ENGINE: history.csv not found.");
        }
    }

    public PredictionResult getPrediction(String productName, String weatherCondition, double temp) {
        // 1. Prepare the inputs
        String target = productName.trim().replace(" ", "");

        // Use the weather passed from the Controller (God Mode)
        WeatherState current = new WeatherState(weatherCondition, temp);

        // 2. Level 1: Perfect Match (Name + Weather + Temp +/- 5)
        List<SalesRecord> matches = history.stream()
                .filter(r -> r.product.equalsIgnoreCase(target))
                .filter(r -> r.weather.equalsIgnoreCase(current.condition))
                .filter(r -> Math.abs(r.temp - current.temp) <= 5.0)
                .collect(Collectors.toList());

        // 3. Level 2: Weather Match Only (Ignore Temp)
        if (matches.isEmpty()) {
            matches = history.stream()
                    .filter(r -> r.product.equalsIgnoreCase(target))
                    .filter(r -> r.weather.equalsIgnoreCase(current.condition))
                    .collect(Collectors.toList());
        }

        // 4. Level 3: Product Match Only (Ignore Weather)
        // This ensures you see data even if today's weather is unique
        if (matches.isEmpty()) {
            matches = history.stream()
                    .filter(r -> r.product.equalsIgnoreCase(target))
                    .collect(Collectors.toList());
        }

        // 5. Final Result
        if (matches.isEmpty()) {
            // "No Data" status
            return new PredictionResult(productName, 0, 0, 0, "No Data");
        }

        double avg = matches.stream().mapToInt(r -> r.quantity).average().orElse(0);
        int rec = (int) Math.ceil(avg * 1.1);

        return new PredictionResult(productName, avg, rec, matches.size(), "OK");
    }

    // --- HELPER METHODS ---

    private String getEmoji(String weather) {
        if (weather.contains("Rain")) return "🌧️";
        if (weather.contains("Snow")) return "❄️";
        if (weather.contains("Sunny")) return "☀️";
        return "☁️";
    }

    private static class SalesRecord {
        String weather;
        double temp;
        String product;
        int quantity;

        public SalesRecord(String w, double t, String p, int q) {
            this.weather = w;
            this.temp = t;
            this.product = p;
            this.quantity = q;
        }
    }

    private static class WeatherState {
        String condition;
        double temp;
        public WeatherState(String c, double t) {
            this.condition = c;
            this.temp = t;
        }
    }

    public static class PredictionResult {
        public String productName;
        public double avgSales;
        public int recommended;
        public int dataPoints; // How many past days we found
        public String status;  // e.g., "Enough Data" or "New Item"

        public PredictionResult(String name, double avg, int rec, int points, String stat) {
            this.productName = name;
            this.avgSales = avg;
            this.recommended = rec;
            this.dataPoints = points;
            this.status = stat;
        }
    }
}