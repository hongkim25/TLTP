package com.hong.thebaker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class PredictionService {

    // The "Brain" (Holds the weights for each product)
    private final Map<String, ModelData> productModels = new HashMap<>();

    private static class ModelData {
        double baseBias;
        double weekendImpact;
        double rainImpact;
        double tempImpact;
    }

    // 1. LOAD AI MODEL (Replaces CSV loading)
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
                model.weekendImpact = weights.get("weekend_impact").asDouble();
                model.rainImpact = weights.get("rain_impact").asDouble();
                model.tempImpact = weights.get("temp_impact").asDouble();

                // Clean name for lookup (remove spaces, match your old logic)
                String key = productName.replace(" ", "").trim();
                productModels.put(key, model);
            });

            System.out.println("✅ AI Model Loaded: " + productModels.size() + " formulas ready.");

        } catch (Exception e) {
            System.err.println("⚠️ AI Model Load Failed (Is train_model.py run?): " + e.getMessage());
        }
    }

    // 2. PREDICT (Uses y = mx + b)
    public PredictionResult getPrediction(String productName, String weather, double temp) {

        // Match the key format (remove spaces)
        String lookupKey = productName.replace(" ", "").trim();
        ModelData model = productModels.get(lookupKey);

        // Fallback if AI has no data for this specific product
        if (model == null) {
            return new PredictionResult(productName, 0, 0, 0, "데이터 부족 (AI)");
        }

        // --- THE AI MATH ---
        boolean isRain = weather.toLowerCase().contains("rain") || weather.toLowerCase().contains("snow");

        // Target is TOMORROW
        DayOfWeek tomorrow = LocalDate.now().plusDays(1).getDayOfWeek();
        boolean isWeekend = (tomorrow == DayOfWeek.SATURDAY || tomorrow == DayOfWeek.SUNDAY);

        // FORMULA: Prediction = Base + (Weekend?) + (Rain?) + (Temp * Weight)
        double predictedValue = model.baseBias
                + (isWeekend ? model.weekendImpact : 0)
                + (isRain ? model.rainImpact : 0)
                + (temp * model.tempImpact);

        // Safety: No negative sales
        int recommended = (int) Math.max(0, Math.round(predictedValue));

        // --- CONTEXT GENERATION ---
        // We explain *why* the number is what it is
        String status = "Normal";
        if (isWeekend && model.weekendImpact > 2.0) status = "Weekend Boost";
        if (isRain && model.rainImpact < -2.0) status = "Rain Drop";
        if (temp > 25 && model.tempImpact > 0.5) status = "Hot Weather Spike";

        // Map to the Old Class Structure so HTML doesn't break
        // baseBias acts as the "Average"
        // dataPoints is set to 365 (since model trained on full year)
        return new PredictionResult(productName, model.baseBias, recommended, 365, status);
    }

    // --- DTO (Kept matching your OLD structure) ---
    public static class PredictionResult {
        public String productName;
        public double avgSales;   // We use Base Bias here
        public int recommended;   // The AI Prediction
        public int dataPoints;    // Dummy value (Model represents all data)
        public String status;

        public PredictionResult(String name, double avg, int rec, int points, String stat) {
            this.productName = name;
            this.avgSales = avg;
            this.recommended = rec;
            this.dataPoints = points;
            this.status = stat;
        }

        // Helper for UI Color (You can add this to HTML: th:classappend="${item.getColor()}")
        public String getColor() {
            if (status.contains("Boost") || status.contains("Spike")) return "green";
            if (status.contains("Drop") || status.contains("Rain")) return "red";
            return "gray";
        }
    }
}