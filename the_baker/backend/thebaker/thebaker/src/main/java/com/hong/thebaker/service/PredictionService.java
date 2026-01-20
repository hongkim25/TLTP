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

    // 2. PREDICTION LOGIC (Cascade Strategy)
    public String predictSales(String productName) {
        // Prepare Input: Remove spaces to match the loaded data
        String target = productName.trim().replace(" ", "");

        // Mock Weather (You can change this to test different conditions)
        WeatherState current = new WeatherState("Cloudy", 2.0);

        // --- LEVEL 1: The "Perfect Match" (Same Weather + Temp +/- 5) ---
        List<SalesRecord> matches = history.stream()
                .filter(r -> r.product.equalsIgnoreCase(target))
                .filter(r -> r.weather.equalsIgnoreCase(current.condition))
                .filter(r -> Math.abs(r.temp - current.temp) <= 5.0)
                .collect(Collectors.toList());

        // --- LEVEL 2: The "Weather Match" (Ignore Temp) ---
        // If it's winter but your data is from summer, Level 1 fails.
        // So we try matching just "Rainy" or "Sunny" regardless of temp.
        if (matches.isEmpty()) {
            matches = history.stream()
                    .filter(r -> r.product.equalsIgnoreCase(target))
                    .filter(r -> r.weather.equalsIgnoreCase(current.condition))
                    .collect(Collectors.toList());
        }

        // --- LEVEL 3: The "Product Only" (Ignore Weather) ---
        // If we have NO data for "Rainy" days for this item, just give the global average.
        // This ensures the button ALWAYS works.
        if (matches.isEmpty()) {
            matches = history.stream()
                    .filter(r -> r.product.equalsIgnoreCase(target))
                    .collect(Collectors.toList());
        }

        // Final Check
        if (matches.isEmpty()) {
            return "데이터 없음 (New Item)";
        }

        double average = matches.stream().mapToInt(r -> r.quantity).average().orElse(0);
        int recommended = (int) Math.ceil(average * 1.1); // +10% Buffer

        return String.format(
                "%s [AI 분석] 현재: %.1f°C (%s)\n" +
                        "• 참조 데이터: %d건\n" +
                        "• 평균 판매: %.1f개\n" +
                        "• 🔥 추천: %d개",
                getEmoji(current.condition), current.temp, current.condition,
                matches.size(), average, recommended
        );
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
}