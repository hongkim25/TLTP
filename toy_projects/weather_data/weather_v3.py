import requests
import csv

# CONFIGURATION
LAT = 36.3504  # Daejeon
LON = 127.3845 
START_DATE = "2022-07-01"
END_DATE = "2025-01-19"

def get_weather_label(code):
    # --- BUSINESS LOGIC MAPPING ---
    
    # 0 = Clear, 1 = Mainly Clear, 2 = Partly Cloudy
    # REALITY: For a bakery, all of these are "Sunny/Nice Days"
    if code in [0, 1, 2]: 
        return "Sunny"
    
    # 3 = Overcast, 45/48 = Fog
    # REALITY: Gloomy days where people might stay inside
    if code in [3, 45, 48]: 
        return "Cloudy"
    
    # 51-55 = Drizzle, 61-65 = Rain, 80-82 = Showers
    # REALITY: You might want to separate "Drizzle" later, but for now "Rain" is safer
    if code in [51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82]: 
        return "Rain"
    
    # 71-77 = Snow, 85-86 = Snow Showers
    if code in [71, 73, 75, 77, 85, 86]: 
        return "Snow"
        
    return "Cloudy" # Fallback

def fetch_history():
    print(f"📡 Fetching Smart Weather Data for Daejeon...")
    
    url = f"https://archive-api.open-meteo.com/v1/archive?latitude={LAT}&longitude={LON}&start_date={START_DATE}&end_date={END_DATE}&daily=weather_code,temperature_2m_max&timezone=auto"
    
    try:
        response = requests.get(url)
        data = response.json()
        
        dates = data['daily']['time']
        codes = data['daily']['weather_code']
        temps = data['daily']['temperature_2m_max']
        
        filename = "history.csv" # Saving directly as history.csv for you
        
        with open(filename, 'w', newline='', encoding='utf-8') as f:
            writer = csv.writer(f)
            writer.writerow(["date", "weather", "temp_high"]) 
            
            # Counter for sanity check
            stats = {"Sunny": 0, "Cloudy": 0, "Rain": 0, "Snow": 0}
            
            for date, code, temp in zip(dates, codes, temps):
                label = get_weather_label(code)
                stats[label] += 1
                writer.writerow([date, label, temp])
                
        print(f"✅ SUCCESS! Data saved to '{filename}'")
        print("--- NEW STATS (Sanity Check) ---")
        print(f"☀️ Sunny: {stats['Sunny']}")
        print(f"☁️ Cloudy: {stats['Cloudy']}")
        print(f"🌧️ Rain:  {stats['Rain']}")
        print(f"❄️ Snow:  {stats['Snow']}")
        
    except Exception as e:
        print(f"❌ Error: {e}")

if __name__ == "__main__":
    fetch_history()