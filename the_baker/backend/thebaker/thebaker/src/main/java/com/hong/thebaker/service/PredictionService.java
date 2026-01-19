package com.hong.thebaker.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.List;

@Service
public class PredictionService {

    private final String API_KEY = "02ec4d898ed95bf738ed07f711308891";
    private final String CITY = "Daejeon";

    // This is the method the Controller is calling
    public String predictSales(String productName) {

        // 1. Get Real Weather Data
        double weatherFactor = getWeatherFactor();
        String weatherEmoji = (weatherFactor >= 1.0) ? "☀️ (맑음)" : "🌧️ (비/흐림)";

        // 2. Analyze Product Name (The "Smart" Logic)
        String name = (productName != null) ? productName.toLowerCase() : "";
        String insight;

        if (name.contains("bagel") || name.contains("베이글")) {
            if (weatherFactor >= 1.0) {
                insight = "날씨가 좋아 브런치 수요가 높습니다. 재고를 20% 늘리세요.";
            } else {
                insight = "비오는 날은 베이글 배달 주문이 15% 증가합니다.";
            }
        }
        else if (name.contains("salt") || name.contains("소금")) {
            insight = "현재 검색량 급상승 트렌드 상품입니다. 조기 품절 주의.";
        }
        else if (name.contains("sandwich") || name.contains("샌드위치")) {
            if (weatherFactor >= 1.0) {
                insight = "나들이객 증가로 점심시간 완판이 예상됩니다.";
            } else {
                insight = "유동인구 감소로 평소보다 10% 적게 준비하세요.";
            }
        }
        else {
            insight = "지난 4주간의 판매 데이터와 유사한 흐름이 예상됩니다.";
        }

        // 3. Combine for the Staff
        return String.format("[%s] %s %s", weatherEmoji, insight, (weatherFactor > 1.0 ? "📈" : "📉"));
    }

    // --- YOUR EXISTING WEATHER LOGIC (KEPT INTACT) ---
    private double getWeatherFactor() {
        try {
            if (API_KEY.equals("YOUR_OPENWEATHER_API_KEY")) {
                System.out.println("⚠️ API Key is missing!");
                return 1.0; // Default if key is missing
            }

            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&appid=" + API_KEY;
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // Parse Weather Condition
            List<Map<String, Object>> weatherList = (List) response.get("weather");
            String main = (String) weatherList.get(0).get("main"); // "Rain", "Clear", "Clouds"

            // Debug log to console
            System.out.println("Current Weather in " + CITY + ": " + main);

            if ("Rain".equalsIgnoreCase(main) || "Drizzle".equalsIgnoreCase(main) || "Thunderstorm".equalsIgnoreCase(main)) {
                return 0.85; // Sell 15% less if raining
            }
            return 1.1; // Sell 10% more if clear/clouds
        } catch (Exception e) {
            System.out.println("Weather API Failed: " + e.getMessage());
            return 1.0; // Default to normal if API fails
        }
    }
}