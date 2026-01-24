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

                JsonNode weights = data.get("weights");
                weights.fields().forEachRemaining(w -> {
                    model.weights.put(w.getKey(), w.getValue().asDouble());
                });

                String key = productName.replace(" ", "").trim();
                productModels.put(key, model);
            });

            System.out.println("✅ AI Model Loaded: " + productModels.size() + " formulas ready.");

        } catch (Exception e) {
            System.err.println("⚠️ AI Model Load Failed: " + e.getMessage());
        }
    }

    public PredictionResult getPrediction(String productName, String weather, double temp) {
        String lookupKey = productName.replace(" ", "").trim();
        ModelData model = productModels.get(lookupKey);

        // Fallback if no data
        if (model == null) {
            return new PredictionResult(productName, 0.0, 0, "데이터 부족", 0.0, 0.0, 0.0);
        }

        // --- 1. DEFINE THE VARIABLES (This was likely missing) ---
        String dayName = LocalDate.now().plusDays(1).getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH); // e.g., "Saturday"
        String dayKey = "day_" + dayName;                        // e.g., "day_Saturday"

        // --- 2. CALCULATE IMPACTS ---
        double dayEffect = model.weights.getOrDefault(dayKey, 0.0);

        double rainImpact = 0.0;
        boolean isRain = weather.toLowerCase().contains("rain") || weather.toLowerCase().contains("snow");
        if (isRain) {
            rainImpact = model.weights.getOrDefault("is_rain", 0.0);
        }

        // Ensure 'temp' key matches Python (check if Python saves as 'temp' or 'temp_impact')
        // Based on your script, Python saves column names like 'temp'.
        double tempImpact = temp * model.weights.getOrDefault("temp", 0.0);

        // --- 3. THE FORMULA ---
        double predictedValue = model.baseBias + dayEffect + rainImpact + tempImpact;
        int recommended = (int) Math.max(0, Math.round(predictedValue));

        // --- 4. STATUS LOGIC ---
        String status = "Normal";
        if (dayEffect > 3.0) status = dayName + " Boost";
        else if (dayEffect < -2.0) status = dayName + " Drop";

        if (rainImpact < -2.0) status = "Rain Drop";
        if (temp > 25 && tempImpact > 0.5) status = "Heat Spike";

        // --- 5. RETURN RESULT ---
        return new PredictionResult(
                productName,
                model.baseBias,
                recommended,
                status,
                dayEffect,
                rainImpact,
                tempImpact
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

        public PredictionResult(String name, double base, int rec, String stat, double day, double rain, double temp) {
            this.productName = name;
            this.baseScore = base;
            this.recommended = rec;
            this.status = stat;
            this.dayEffect = day;
            this.rainEffect = rain;
            this.tempEffect = temp;
        }

        public String getColor() {
            if (status.contains("Boost") || status.contains("Spike")) return "green";
            if (status.contains("Drop") || status.contains("Rain")) return "red";
            return "gray";
        }
    }
}