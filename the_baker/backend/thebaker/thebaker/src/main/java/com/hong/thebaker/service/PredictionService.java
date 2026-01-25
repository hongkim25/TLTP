package com.hong.thebaker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class PredictionService {

    private final Map<String, ModelData> productModels = new HashMap<>();

    private static class ModelData {
        double baseBias;
        Map<String, Double> weights = new HashMap<>();
        double wasteRisk;
        double avgMade;
    }

    @PostConstruct
    public void loadModel() {
        try {
            InputStream is = new ClassPathResource("ml_model.json").getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);

            root.fields().forEachRemaining(entry -> {
                String productName = entry.getKey();
                JsonNode data = entry.getValue();

                ModelData model = new ModelData();
                model.baseBias = data.get("base_bias").asDouble();
                model.wasteRisk = data.has("waste_risk") ? data.get("waste_risk").asDouble() : 0.0;
                model.avgMade = data.has("avg_made") ? data.get("avg_made").asDouble() : 0.0;

                data.get("weights").fields().forEachRemaining(w ->
                        model.weights.put(w.getKey(), w.getValue().asDouble())
                );

                String key = productName.replace(" ", "").trim();
                productModels.put(key, model);
            });
            System.out.println("✅ AI Model Loaded.");

        } catch (Exception e) {
            System.err.println("⚠️ AI Model Load Failed: " + e.getMessage());
        }
    }

    public PredictionResult getPrediction(String productName, String weather, double temp) {
        String lookupKey = productName.replace(" ", "").trim();
        ModelData model = productModels.get(lookupKey);

        if (model == null) return new PredictionResult(productName, 0, 0, "No Data", 0,0,0,0,0);

        // 1. Calculations
        String dayName = LocalDate.now().plusDays(1).getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String dayKey = "day_" + dayName;
        boolean isRain = weather.toLowerCase().contains("rain") || weather.toLowerCase().contains("snow");

        double dayEffect = model.weights.getOrDefault(dayKey, 0.0);
        double rainImpact = isRain ? model.weights.getOrDefault("is_rain", 0.0) : 0.0;
        double tempImpact = temp * model.weights.getOrDefault("temp", 0.0);

        double predictedSales = model.baseBias + dayEffect + rainImpact + tempImpact;
        int recommended = (int) Math.max(0, Math.round(predictedSales));

        // 2. STATUS LOGIC (Updated for Sensitivity)
        String status = ""; // Default is empty (Clean UI)

        // Threshold lowered from 3.0 to 1.0 (More sensitive)
        if (dayEffect >= 1.0) status = dayName + " Boost";
        else if (dayEffect <= -1.0) status = dayName + " Drop";

        // Rain Logic Restored
        if (rainImpact <= -1.0) status = "Rain Drop";
        if (temp > 25 && tempImpact >= 1.0) status = "Heat Spike";

        // Fallback: If status is still empty but we have a badge slot, keep it "Normal" or empty.
        if (status.isEmpty()) status = "Stable";

        return new PredictionResult(
                productName, model.baseBias, recommended, status,
                dayEffect, rainImpact, tempImpact,
                model.wasteRisk,
                model.avgMade
        );
    }

    // DTO Class
    public static class PredictionResult {
        public String productName;
        public double baseScore;
        public int recommended;
        public String status;
        public double dayEffect;
        public double rainEffect;
        public double tempEffect;
        public double wasteRisk;
        public double avgMade;

        public PredictionResult(String name, double base, int rec, String stat,
                                double day, double rain, double temp, double waste, double made) {
            this.productName = name;
            this.baseScore = base;
            this.recommended = rec;
            this.status = stat;
            this.dayEffect = day;
            this.rainEffect = rain;
            this.tempEffect = temp;
            this.wasteRisk = waste;
            this.avgMade = made;
        }

        // Logic for Colors in HTML
        public String getColor() {
            if (status.contains("Boost") || status.contains("Spike")) return "green";
            if (status.contains("Drop") || status.contains("Rain")) return "red";
            return "gray"; // Stable = Gray
        }
    }
}