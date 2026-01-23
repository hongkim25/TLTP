import pandas as pd
import json
import os

# 1. SETUP PATHS
script_dir = os.path.dirname(os.path.abspath(__file__))
csv_path = os.path.join(script_dir, '..', 'src', 'main', 'resources', 'history.csv')
json_path = os.path.join(script_dir, '..', 'src', 'main', 'resources', 'seasonality.json')

print(f"📂 Reading Data from: {csv_path}")

try:
    # 2. LOAD DATA (Strict Index Mode)
    # 0=Date, 1=Name, 2=Qty, 4=Weather, 5=Temp
    df = pd.read_csv(csv_path, header=0, usecols=[0, 1, 2, 4, 5])
    df.columns = ['date', 'product_type', 'quantity_sold', 'weather', 'temp_avg']

    # 3. CLEANING
    # Handle Date Formats (YYYYMMDD or YYYY-MM-DD)
    df['date'] = pd.to_datetime(df['date'], format='%Y%m%d', errors='coerce')

    # Fallback if first attempt failed (e.g., if date was 2024-01-01)
    if df['date'].isnull().all():
        print("⚠️ switching to auto-date detection...")
        df = pd.read_csv(csv_path, header=0, usecols=[0, 1, 2, 4, 5])
        df.columns = ['date', 'product_type', 'quantity_sold', 'weather', 'temp_avg']
        df['date'] = pd.to_datetime(df['date'], errors='coerce')

    df = df.dropna(subset=['date'])
    df['product_type'] = df['product_type'].astype(str).str.replace(" ", "")

    # 4. CALCULATE SEASONALITY (Using NUMBERS, not Names)
    # 0=Monday, 6=Sunday
    df['day_num'] = df['date'].dt.dayofweek

    global_means = df.groupby('product_type')['quantity_sold'].mean()
    daily_means = df.groupby(['product_type', 'day_num'])['quantity_sold'].mean()

    # Map numbers to English names for JSON
    day_map = {0: 'Monday', 1: 'Tuesday', 2: 'Wednesday', 3: 'Thursday',
               4: 'Friday', 5: 'Saturday', 6: 'Sunday'}

    seasonality_factors = {}

    for product in global_means.index:
        base_sales = global_means[product]
        if base_sales == 0: continue

        product_factors = {}

        # Loop through 0 to 6 (Mon to Sun)
        for day_num in range(7):
            day_name = day_map[day_num]
            try:
                # Try to get average for this specific day number
                day_avg = daily_means.loc[product, day_num]

                # Calculate Factor
                factor = day_avg / base_sales

                # ROUND it to 2 decimals
                product_factors[day_name] = round(factor, 2)
            except KeyError:
                # If product never sold on this day, use 1.0 (Neutral)
                product_factors[day_name] = 1.0

        seasonality_factors[product] = {
            "base_avg": round(base_sales, 1),
            "factors": product_factors
        }

    # 5. SAVE
    with open(json_path, 'w', encoding='utf-8') as f:
        json.dump(seasonality_factors, f, indent=4, ensure_ascii=False)

    print(f"✅ SUCCESS! Intelligence saved.")

    # DEBUG: Print one example to prove it changed
    test_key = list(seasonality_factors.keys())[0]
    print(f"👉 Check {test_key}: {seasonality_factors[test_key]['factors']}")

except Exception as e:
    print(f"❌ ERROR: {e}")