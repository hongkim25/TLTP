import requests
import csv
from collections import Counter

# CONFIGURATION
LAT = 36.3504 
LON = 127.3845
START_DATE = "2022-07-01"
END_DATE = "2026-01-19"

# BAKERY HOURS
OPEN_HOUR = 9  # 9 AM
CLOSE_HOUR = 15 # 3 PM

def get_dominant_weather(hourly_codes):
    """
    Analyzes weather ONLY between 9 AM and 3 PM.
    """
    rain_count = 0
    snow_count = 0
    cloud_count = 0
    sun_count = 0
    
    for code in hourly_codes:
        if code in [71, 73, 75, 77, 85, 86]: snow_count += 1
        elif code in [51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82]: rain_count += 1
        elif code in [3, 45, 48]: cloud_count += 1
        elif code in [0, 1, 2]: sun_count += 1

    if snow_count >= 2: return "Snow"
    if rain_count >= 2: return "Rain"
    if sun_count >= cloud_count: return "Sunny"
    return "Cloudy"

def fetch_history():
    print(f"📡 Fetching HOURLY Weather (Business Hours: {OPEN_HOUR}:00 - {CLOSE_HOUR}:00)...")
    
    url = f"https://archive-api.open-meteo.com/v1/archive?latitude={LAT}&longitude={LON}&start_date={START_DATE}&end_date={END_DATE}&hourly=weather_code,temperature_2m&timezone=auto"
    
    try:
        response = requests.get(url)
        data = response.json()
        
        times = data['hourly']['time']
        codes = data['hourly']['weather_code']
        temps = data['hourly']['temperature_2m']
        
        daily_data = {}
        
        for t, c, temp in zip(times, codes, temps):
            date_part, time_part = t.split("T")
            hour = int(time_part.split(":")[0])
            
            # FILTER: ONLY KEEP BUSINESS HOURS
            if OPEN_HOUR <= hour <= CLOSE_HOUR:
                if date_part not in daily_data:
                    daily_data[date_part] = {"codes": [], "temps": []}
                daily_data[date_part]["codes"].append(c)
                daily_data[date_part]["temps"].append(temp)
        
        filename = "history.csv"
        
        with open(filename, 'w', newline='', encoding='utf-8') as f:
            writer = csv.writer(f)
            # Header now includes all metrics
            writer.writerow(["date", "weather", "temp_avg", "temp_max", "temp_min"]) 
            
            for date, info in daily_data.items():
                if not info["codes"]: continue
                
                weather_label = get_dominant_weather(info["codes"])
                
                # --- NEW MATH ---
                # Calculate statistics only for business hours
                avg_temp = round(sum(info["temps"]) / len(info["temps"]), 1)
                max_temp = max(info["temps"])
                min_temp = min(info["temps"])
                
                # We save AVG as the 3rd column so Java picks it up automatically
                writer.writerow([date, weather_label, avg_temp, max_temp, min_temp])
                
        print(f"✅ SUCCESS! Data saved to '{filename}'")
        print("👉 Columns: Date, Weather, AvgTemp, MaxTemp, MinTemp")
        print("Example Row (Java will read AvgTemp): 2024-01-15, Snow, -1.5, 2.0, -4.0")
        
    except Exception as e:
        print(f"❌ Error: {e}")

if __name__ == "__main__":
    fetch_history()