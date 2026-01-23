import pandas as pd
import os

script_dir = os.path.dirname(os.path.abspath(__file__))
csv_path = os.path.join(script_dir, '..', 'src', 'main', 'resources', 'history.csv')

print(f"Checking data in: {csv_path}")

# Load raw
df = pd.read_csv(csv_path, header=0, usecols=[0], names=['raw_date'])

total_rows = len(df)
print(f"📄 Total Rows Found: {total_rows}")

# Try to convert dates
df['valid_date'] = pd.to_datetime(df['raw_date'], format='%Y%m%d', errors='coerce')

# Count failures
bad_rows = df[df['valid_date'].isnull()]
bad_count = len(bad_rows)

print(f"❌ Invalid Dates Found: {bad_count}")

if bad_count > 0:
    print("\n⚠️ EXAMPLES OF BAD DATA (Fix these in history.csv):")
    print(bad_rows['raw_date'].head(5).to_string(index=False))
else:
    print("✅ All dates are valid. The 1.0 issue is something else.")