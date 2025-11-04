import pandas as pd
from gtts import gTTS
from tqdm import tqdm
import os
import subprocess
import time

# Create output folders
os.makedirs("temp_spanish", exist_ok=True)
os.makedirs("temp_english", exist_ok=True)
os.makedirs("audio_files", exist_ok=True)

# Load your vocabulary
df = pd.read_csv("voca.txt", sep="\t", names=["spanish", "english"], 
                 on_bad_lines='skip', encoding='utf-8')

print(f"Generating audio for {len(df)} words...")

for idx, row in tqdm(df.iterrows(), total=len(df)):
    spanish_word = str(row['spanish']).strip()
    english_meaning = str(row['english']).strip()
    
    # Skip if already exists (resume capability)
    safe_filename = spanish_word.replace(' ', '_').replace('/', '_')[:50]
    output_file = f"audio_files/{idx+1:04d}_{safe_filename}.mp3"
    
    if os.path.exists(output_file):
        continue  # Skip already processed files
    
    # Retry logic for network issues
    max_retries = 3
    for attempt in range(max_retries):
        try:
            # Generate Spanish audio
            spanish_tts = gTTS(text=spanish_word, lang='es', slow=False)
            spanish_file = f"temp_spanish/{idx:04d}.mp3"
            spanish_tts.save(spanish_file)
            
            # Generate English audio
            english_tts = gTTS(text=english_meaning, lang='en', slow=False)
            english_file = f"temp_english/{idx:04d}.mp3"
            english_tts.save(english_file)
            
            # Combine using ffmpeg
            subprocess.run([
                'ffmpeg', '-y', '-i', spanish_file, '-i', english_file,
                '-filter_complex', 
                '[0:a][1:a]concat=n=2:v=0:a=1,adelay=750|750[out]',
                '-map', '[out]', output_file
            ], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            
            # Clean up temp files
            if os.path.exists(spanish_file):
                os.remove(spanish_file)
            if os.path.exists(english_file):
                os.remove(english_file)
            
            break  # Success, exit retry loop
            
        except Exception as e:
            if attempt < max_retries - 1:
                print(f"\nRetrying word {idx} (attempt {attempt+2}/{max_retries})...")
                time.sleep(2)  # Wait before retry
            else:
                print(f"\nFailed on word {idx}: {spanish_word}. Skipping...")
    
    time.sleep(0.2)  # Increased to avoid rate limiting

print("✅ Done! Cleaning up temp folders...")
os.system('rm -rf temp_spanish temp_english')
print("✅ All done! Check the 'audio_files' folder")