import pandas as pd
import json
import os
from sklearn.linear_model import LinearRegression

# 1. SETUP
script_dir = os.path.dirname(os.path.abspath(__file__))
csv_path = os.path.join(script_dir, '..', 'src', 'main', 'resources', 'history.csv')
model_path = os.path.join(script_dir, '..', 'src', 'main', 'resources', 'ml_model.json')

print(f"🧠 Training AI Model (Day-Specific)...")

# 2. LOAD & CLEAN
try:
    df = pd.read_csv(csv_path, header=0, usecols=[0, 1, 2, 4, 5])
    df.columns = ['date', 'product', 'qty', 'weather', 'temp']
    df['date'] = pd.to_datetime(df['date'], errors='coerce')
    df = df.dropna(subset=['date'])
except Exception as e:
    print(f"❌ Error: {e}")
    exit()

# 3. FEATURE ENGINEERING (One-Hot Encoding for Days)
# Create columns: day_Monday, day_Tuesday...
df['day_name'] = df['date'].dt.day_name()
days_dummies = pd.get_dummies(df['day_name'], prefix='day')
df = pd.concat([df, days_dummies], axis=1)

# Ensure ALL days exist (fill with 0 if missing)
all_days = ['day_Monday', 'day_Tuesday', 'day_Wednesday', 'day_Thursday', 'day_Friday', 'day_Saturday', 'day_Sunday']
for day in all_days:
    if day not in df.columns:
        df[day] = 0

df['is_rain'] = df['weather'].astype(str).apply(lambda x: 1 if 'Rain' in x or 'Snow' in x else 0)

# 4. TRAIN
model_output = {}
products = df['product'].unique()

for p in products:
    subset = df[df['product'] == p].copy()
    if len(subset) < 2: continue

    # Features: All 7 days + Rain + Temp
    feature_cols = all_days + ['is_rain', 'temp']
    X = subset[feature_cols]
    y = subset['qty']

    try:
        reg = LinearRegression()
        reg.fit(X, y)

        # Save Weights
        weights = {}
        # Map the coefficients back to their names
        for i, col_name in enumerate(feature_cols):
            weights[col_name] = round(reg.coef_[i], 2)

        model_output[p] = {
            "base_bias": round(reg.intercept_, 2),
            "weights": weights
        }
    except Exception as e:
        print(f"Skipping {p}: {e}")

# 5. SAVE
with open(model_path, 'w', encoding='utf-8') as f:
    json.dump(model_output, f, indent=4, ensure_ascii=False)

print("✅ Logic Updated: Specific Day Weights Saved.")