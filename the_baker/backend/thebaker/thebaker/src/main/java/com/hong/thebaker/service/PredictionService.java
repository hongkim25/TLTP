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

    // CHANGED: We now use a Map for weights to handle dynamic keys like "day_Monday"
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

                // DYNAMIC LOADING: Read whatever keys Python sent us (day_Mon, rain_impact, etc.)
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

        if (model == null) {
            return new PredictionResult(productName, 0, 0, 0, "데이터 부족");
        }

        // 1. Get Tomorrow's Day Name (e.g., "day_Saturday")
        // We match the format used in Python (day_ + Full English Name)
        String dayName = LocalDate.now().plusDays(1).getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String dayKey = "day_" + dayName;

        // 2. Look up the specific weights
        double dayEffect = model.weights.getOrDefault(dayKey, 0.0);
        double rainImpact = model.weights.getOrDefault("is_rain", 0.0); // Make sure Python calls it 'is_rain'
        double tempImpact = model.weights.getOrDefault("temp", 0.0);

        boolean isRain = weather.toLowerCase().contains("rain") || weather.toLowerCase().contains("snow");

        // 3. The Formula
        double predictedValue = model.baseBias
                + dayEffect
                + (isRain ? rainImpact : 0)
                + (temp * tempImpact);

        int recommended = (int) Math.max(0, Math.round(predictedValue));

        // 4. Generate Context (Why?)
        String status = "Normal";
        if (dayEffect > 5.0) status = dayName + " Boost"; // e.g., "Saturday Boost"
        if (dayEffect < -2.0) status = dayName + " Drop"; // e.g., "Monday Drop"
        if (isRain && rainImpact < -2.0) status = "Rain Drop";
        if (temp > 25 && tempImpact > 0.5) status = "Heat Spike";

        return new PredictionResult(productName, model.baseBias, recommended, 365, status);
    }

    // Keep the DTO exactly as before so HTML doesn't break
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

        public String getColor() {
            if (status.contains("Boost") || status.contains("Spike")) return "green";
            if (status.contains("Drop") || status.contains("Rain")) return "red";
            return "gray";
        }
    }
}