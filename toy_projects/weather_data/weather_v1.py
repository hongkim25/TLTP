import requests
import csv
from datetime import datetime

# CONFIGURATION
LAT = 36.3504  # Daejeon Latitude
LON = 127.3845 # Daejeon Longitude
START_DATE = "2022-07-01"
END_DATE = "2025-01-19" # Up to today

def get_weather_label(code):
    """Maps WMO Weather Codes to our Simple CSV Format"""
    # Codes: https://open-meteo.com/en/docs
    if code == 0: return "Clear"
    if code in [1, 2, 3]: return "Clouds"
    if code in [51, 53, 55, 61, 63, 65, 80, 81, 82]: return "Rain"
    if code in [71, 73, 75, 77, 85, 86]: return "Snow"
    return "Clouds" # Default for fog/etc

def fetch_history():
    print(f"📡 Fetching weather data for Daejeon ({START_DATE} to {END_DATE})...")
    
    url = f"https://archive-api.open-meteo.com/v1/archive?latitude={LAT}&longitude={LON}&start_date={START_DATE}&end_date={END_DATE}&daily=weather_code&timezone=auto"
    
    try:
        response = requests.get(url)
        data = response.json()
        
        dates = data['daily']['time']
        codes = data['daily']['weather_code']
        
        # Write to CSV
        filename = "daejeon_weather_3years.csv"
        with open(filename, 'w', newline='', encoding='utf-8') as f:
            writer = csv.writer(f)
            writer.writerow(["date", "weather"]) # Header
            
            for date, code in zip(dates, codes):
                label = get_weather_label(code)
                writer.writerow([date, label])
                
        print(f"✅ SUCCESS! Saved {len(dates)} days of weather to '{filename}'")
        print("👉 Now open this file and paste your brother's sales numbers next to it.")
        
    except Exception as e:
        print(f"❌ Error: {e}")

if __name__ == "__main__":
    fetch_history()