import requests
import csv
from collections import Counter

# CONFIGURATION
LAT = 36.3504 
LON = 127.3845
START_DATE = "2022-07-01"
END_DATE = "2025-01-19"

# BAKERY HOURS (24-hour format)
OPEN_HOUR = 9  # 9 AM
CLOSE_HOUR = 15 # 3 PM

def get_dominant_weather(hourly_codes):
    """
    Analyzes weather ONLY between 9 AM and 7 PM.
    Priority: Snow > Rain > Cloudy > Sunny
    """
    rain_count = 0
    snow_count = 0
    cloud_count = 0
    sun_count = 0
    
    for code in hourly_codes:
        # Snow Codes
        if code in [71, 73, 75, 77, 85, 86]: 
            snow_count += 1
        # Rain Codes (Drizzle, Rain, Showers)
        elif code in [51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82]:
            rain_count += 1
        # Cloudy Codes (Overcast, Fog)
        elif code in [3, 45, 48]:
            cloud_count += 1
        # Sunny Codes (Clear, Mainly Clear, Partly Cloudy)
        elif code in [0, 1, 2]:
            sun_count += 1

    # LOGIC: 
    # 1. If it snows for more than 2 hours -> SNOW (Impacts traffic)
    if snow_count >= 2: return "Snow"
    
    # 2. If it rains for more than 2 hours -> RAIN (Impacts walking)
    if rain_count >= 2: return "Rain"
    
    # 3. If it's mostly sunny (more sun than clouds) -> SUNNY
    if sun_count >= cloud_count: return "Sunny"
    
    # 4. Otherwise -> CLOUDY
    return "Cloudy"

def fetch_history():
    print(f"📡 Fetching HOURLY Weather for Daejeon (Analyzing {OPEN_HOUR}:00 - {CLOSE_HOUR}:00)...")
    
    # FETCH HOURLY DATA INSTEAD OF DAILY
    url = f"https://archive-api.open-meteo.com/v1/archive?latitude={LAT}&longitude={LON}&start_date={START_DATE}&end_date={END_DATE}&hourly=weather_code,temperature_2m&timezone=auto"
    
    try:
        response = requests.get(url)
        data = response.json()
        
        times = data['hourly']['time']
        codes = data['hourly']['weather_code']
        temps = data['hourly']['temperature_2m']
        
        # We need to group hourly data by Day
        # Structure: { "2024-01-01": { "codes": [], "temps": [] } }
        daily_data = {}
        
        for t, c, temp in zip(times, codes, temps):
            # t format: "2024-01-01T13:00"
            date_part, time_part = t.split("T")
            hour = int(time_part.split(":")[0])
            
            # FILTER: ONLY KEEP BUSINESS HOURS
            if OPEN_HOUR <= hour <= CLOSE_HOUR:
                if date_part not in daily_data:
                    daily_data[date_part] = {"codes": [], "temps": []}
                daily_data[date_part]["codes"].append(c)
                daily_data[date_part]["temps"].append(temp)
        
        # PROCESS & SAVE
        filename = "history.csv"
        stats = {"Sunny": 0, "Cloudy": 0, "Rain": 0, "Snow": 0}
        
        with open(filename, 'w', newline='', encoding='utf-8') as f:
            writer = csv.writer(f)
            writer.writerow(["date", "weather", "temp_high"]) 
            
            for date, info in daily_data.items():
                if not info["codes"]: continue # Skip empty days
                
                # 1. Calculate Dominant Weather
                weather_label = get_dominant_weather(info["codes"])
                stats[weather_label] += 1
                
                # 2. Calculate Max Temp (during business hours only!)
                max_temp = max(info["temps"])
                
                writer.writerow([date, weather_label, max_temp])
                
        print(f"✅ SUCCESS! Processed {len(daily_data)} days.")
        print("--- NEW BUSINESS-HOUR STATS ---")
        print(f"☀️ Sunny: {stats['Sunny']}")
        print(f"☁️ Cloudy: {stats['Cloudy']}")
        print(f"🌧️ Rain:  {stats['Rain']}")
        print(f"❄️ Snow:  {stats['Snow']}")
        
    except Exception as e:
        print(f"❌ Error: {e}")

if __name__ == "__main__":
    fetch_history()