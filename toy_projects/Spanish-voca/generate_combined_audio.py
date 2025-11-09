import pandas as pd
from gtts import gTTS
from tqdm import tqdm
import os
import subprocess
import time

# Create temp folder
os.makedirs("temp_audio", exist_ok=True)

# Read your file (tab-separated: Spanish | English)
df = pd.read_csv("combined_study.txt", sep="\t", 
                 names=["spanish", "english"],
                 on_bad_lines='skip', encoding='utf-8')

print(f"Generating audio for {len(df)} items...")

# List to store all audio file paths
audio_files = []

for idx, row in tqdm(df.iterrows(), total=len(df)):
    spanish_text = str(row['spanish']).strip()
    english_text = str(row['english']).strip()
    
    max_retries = 3
    for attempt in range(max_retries):
        try:
            # Generate Spanish audio (normal speed - used twice)
            spanish_tts = gTTS(text=spanish_text, lang='es', slow=False)
            spanish_file = f"temp_audio/{idx:04d}_es.mp3"
            spanish_tts.save(spanish_file)
            
            # Generate English audio
            english_tts = gTTS(text=english_text, lang='en', slow=False)
            english_file = f"temp_audio/{idx:04d}_en.mp3"
            english_tts.save(english_file)
            
            # Combine: Spanish + Spanish + English (using same file twice)
            combined_file = f"temp_audio/{idx:04d}_combined.mp3"
            subprocess.run([
                'ffmpeg', '-y', 
                '-i', spanish_file, 
                '-i', spanish_file, 
                '-i', english_file,
                '-filter_complex', 
                '[0:a][1:a][2:a]concat=n=3:v=0:a=1,adelay=700|700[out]',
                '-map', '[out]', combined_file
            ], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            
            # Add to list for final merge
            audio_files.append(combined_file)
            
            # Clean up
            os.remove(spanish_file)
            os.remove(english_file)
            
            break
            
        except Exception as e:
            if attempt < max_retries - 1:
                print(f"\nRetrying item {idx+1} (attempt {attempt+2}/{max_retries})...")
                time.sleep(2)
            else:
                print(f"\nFailed on item {idx+1}. Skipping...")
    
    time.sleep(0.3)

# Create final merge list
print("\nMerging all audio into one file...")
with open("temp_audio/filelist.txt", "w") as f:
    for audio_file in audio_files:
        f.write(f"file '{os.path.basename(audio_file)}'\n")

# Merge everything into one final MP3
subprocess.run([
    'ffmpeg', '-y', '-f', 'concat', '-safe', '0', 
    '-i', 'temp_audio/filelist.txt', 
    '-c', 'copy', 'final_combined_study.mp3'
], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

print("✅ Done! Cleaning up temp files...")
os.system('rm -rf temp_audio')
print("✅ All done! Your file: final_combined_study.mp3")