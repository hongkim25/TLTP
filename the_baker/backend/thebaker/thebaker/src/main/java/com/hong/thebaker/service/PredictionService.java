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
                    String[] parts = line.split(",");

                    // index 1: weather (Sunny, Rain, etc.)
                    String weather = parts[1].trim();

                    // index 2: average temperature (temp_avg)
                    double temp = Double.parseDouble(parts[2].trim());

                    // index 5: product (Raw Korean Name)
                    // We remove spaces to make matching safer (e.g., "소금 빵" == "소금빵")
                    String rawName = parts[5].trim().replace(" ", "");

                    // index 6: quantity
                    int qty = Integer.parseInt(parts[6].trim());

                    history.add(new SalesRecord(weather, temp, rawName, qty));
                } catch (Exception e) {
                    // Skip bad rows
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