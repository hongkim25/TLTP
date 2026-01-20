import requests
import csv

# CONFIGURATION
LAT = 36.3504  # Daejeon Latitude
LON = 127.3845 # Daejeon Longitude
START_DATE = "2022-07-01"
END_DATE = "2025-01-19" # Up to today

def get_weather_label(code):
    # WMO Weather Codes (Simplified)
    if code == 0: return "Clear"
    if code in [1, 2, 3]: return "Clouds"
    if code in [51, 53, 55, 61, 63, 65, 80, 81, 82]: return "Rain"
    if code in [71, 73, 75, 77, 85, 86]: return "Snow"
    return "Clouds"

def fetch_history():
    print(f"📡 Fetching Weather + Temperature for Daejeon...")
    
    # ADDED: temperature_2m_max to the API request
    url = f"https://archive-api.open-meteo.com/v1/archive?latitude={LAT}&longitude={LON}&start_date={START_DATE}&end_date={END_DATE}&daily=weather_code,temperature_2m_max&timezone=auto"
    
    try:
        response = requests.get(url)
        data = response.json()
        
        dates = data['daily']['time']
        codes = data['daily']['weather_code']
        temps = data['daily']['temperature_2m_max'] # NEW DATA
        
        filename = "daejeon_weather_advanced.csv"
        
        with open(filename, 'w', newline='', encoding='utf-8') as f:
            writer = csv.writer(f)
            # NEW HEADER: Date, Weather, Temp
            writer.writerow(["date", "weather", "temp_high"]) 
            
            for date, code, temp in zip(dates, codes, temps):
                label = get_weather_label(code)
                writer.writerow([date, label, temp])
                
        print(f"SUCCESS! Data saved to '{filename}'")
        print("Example Row: 2024-01-15, Snow, -2.5")
        
    except Exception as e:
        print(f"❌ Error: {e}")

if __name__ == "__main__":
    fetch_history()