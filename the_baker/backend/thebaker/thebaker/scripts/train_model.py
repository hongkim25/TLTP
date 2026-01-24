import pandas as pd
import json
import os
from sklearn.linear_model import LinearRegression
import numpy as np

# 1. SETUP PATHS
script_dir = os.path.dirname(os.path.abspath(__file__))
csv_path = os.path.join(script_dir, '..', 'src', 'main', 'resources', 'history.csv')
model_path = os.path.join(script_dir, '..', 'src', 'main', 'resources', 'ml_model.json')

print(f"🧠 Training AI Model from: {csv_path}")

# 2. LOAD DATA
try:
    df = pd.read_csv(csv_path, header=0, usecols=[0, 1, 2, 4, 5])
    df.columns = ['date', 'product', 'qty', 'weather', 'temp']
    print(f"   -> Loaded {len(df)} rows raw.")
except Exception as e:
    print(f"❌ Error reading CSV: {e}")
    exit()

# Fix Dates
# Debug: Show me what the date looks like before crashing
print(f"   👀 Raw Date Sample: {df['date'].iloc[0]}") 

# "Smart" Date Parsing (Auto-detects YYYY-MM-DD or YYYYMMDD)
df['date'] = pd.to_datetime(df['date'], errors='coerce') 

df = df.dropna(subset=['date'])
print(f"   -> {len(df)} rows remain after cleaning dates.")

if len(df) == 0:
    print("❌ CRITICAL: No valid data found. Check date formats in CSV.")
    exit()

# 3. FEATURE ENGINEERING
df['is_weekend'] = df['date'].dt.dayofweek.apply(lambda x: 1 if x >= 5 else 0)
df['is_rain'] = df['weather'].astype(str).apply(lambda x: 1 if 'Rain' in x or 'Snow' in x else 0)

# 4. TRAIN MODEL
model_output = {}
products = df['product'].unique()
print(f"   -> Found products: {products}")

for p in products:
    subset = df[df['product'] == p].copy()
    
    # CHANGED: Lowered limit from 10 to 2 for Demo purposes
    if len(subset) < 2: 
        print(f"   ⚠️ Skipping {p}: Only {len(subset)} rows (Need 2+)")
        continue

    X = subset[['is_weekend', 'is_rain', 'temp']]
    y = subset['qty']

    try:
        reg = LinearRegression()
        reg.fit(X, y)

        model_output[p] = {
            "base_bias": round(reg.intercept_, 2),
            "weights": {
                "weekend_impact": round(reg.coef_[0], 2),
                "rain_impact":    round(reg.coef_[1], 2),
                "temp_impact":    round(reg.coef_[2], 2)
            }
        }
        print(f"   ✅ Trained {p}")
    except Exception as e:
        print(f"   ❌ Failed to train {p}: {e}")

# 5. SAVE
if not model_output:
    print("❌ ERROR: No models were created. Check the 'Skipping' messages above.")
else:
    with open(model_path, 'w', encoding='utf-8') as f:
        json.dump(model_output, f, indent=4, ensure_ascii=False)
    
    print("\n🎉 SUCCESS! AI Model Saved.")
    first_key = list(model_output.keys())[0]
    print(f"👉 Example ({first_key}): {model_output[first_key]}")