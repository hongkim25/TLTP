import pandas as pd
import json
import os
import numpy as np
from sklearn.linear_model import LinearRegression

# 1. SETUP
script_dir = os.path.dirname(os.path.abspath(__file__))
csv_path = os.path.join(script_dir, '..', 'src', 'main', 'resources', 'history.csv')
model_path = os.path.join(script_dir, '..', 'src', 'main', 'resources', 'ml_model.json')

print(f"🧠 Training Dual-Core AI (True Average Logic)...")

# 2. LOAD DATA
try:
    df = pd.read_csv(csv_path, header=0, usecols=[0, 1, 2, 4, 5])
    df.columns = ['date', 'product', 'qty', 'weather', 'temp']
    df['date'] = pd.to_datetime(df['date'], errors='coerce')
    df = df.dropna(subset=['date'])
except Exception as e:
    print(f"❌ Error loading CSV: {e}")
    exit()

# 3. FEATURE ENGINEERING (For Sales Prediction)
df['day_name'] = df['date'].dt.day_name()
days_dummies = pd.get_dummies(df['day_name'], prefix='day')
df = pd.concat([df, days_dummies], axis=1)

all_days = ['day_Monday', 'day_Tuesday', 'day_Wednesday', 'day_Thursday', 'day_Friday', 'day_Saturday', 'day_Sunday']
for day in all_days:
    if day not in df.columns:
        df[day] = 0

df['is_rain'] = df['weather'].astype(str).apply(lambda x: 1 if 'Rain' in x or 'Snow' in x else 0)

# 4. TRAIN MODELS
model_output = {}
products = df['product'].unique()

for p in products:
    subset = df[df['product'] == p].copy()
    if len(subset) < 2: continue

    # --- A. SALES PREDICTION (Smart) ---
    sales_data = subset[subset['qty'] > 0].copy()
    sales_base = 0
    sales_weights = {}

    if len(sales_data) > 2:
        X = sales_data[all_days + ['is_rain', 'temp']]
        y = sales_data['qty']
        try:
            reg = LinearRegression().fit(X, y)
            sales_base = round(reg.intercept_, 2)
            for i, col in enumerate(X.columns):
                sales_weights[col] = round(reg.coef_[i], 2)
        except:
            sales_base = sales_data['qty'].mean()

    # --- B. WASTE STATISTICS (The "X out of Y" Logic) ---
    # 1. Calculate Total Waste (Sum of negatives)
    total_waste = subset[subset['qty'] < 0]['qty'].abs().sum()

    # 2. Calculate Total Sold (Sum of positives)
    total_sold = subset[subset['qty'] > 0]['qty'].sum()

    # 3. Total Made = Sold + Waste
    total_made = total_sold + total_waste

    # 4. Total Days this product was on the shelf
    total_active_days = len(subset)

    # 5. Calculate Averages
    avg_waste = 0.0
    avg_made = 0.0

    if total_active_days > 0:
        avg_waste = round(total_waste / total_active_days, 1)
        avg_made = round(total_made / total_active_days, 1)

    # SAVE TO JSON
    model_output[p] = {
        "base_bias": sales_base,
        "weights": sales_weights,
        "waste_risk": avg_waste, # "Usually 2 thrown away"
        "avg_made": avg_made     # "Usually 20 made"
    }

# 5. WRITE FILE
with open(model_path, 'w', encoding='utf-8') as f:
    json.dump(model_output, f, indent=4, ensure_ascii=False)

print(f"✅ AI Trained. Saved to {model_path}")