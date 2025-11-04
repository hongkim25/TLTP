import pandas as pd
from gtts import gTTS
from tqdm import tqdm
import os
import subprocess
import time

# Create output folders
os.makedirs("temp_spanish", exist_ok=True)
os.makedirs("temp_english", exist_ok=True)
os.makedirs("sentence_audio", exist_ok=True)

# Read sentences from file
with open("sentences.txt", "r", encoding="utf-8") as f:
    lines = f.readlines()

# Parse sentences (remove bullets and split by |)
sentences = []
for line in lines:
    line = line.strip()
    if line and '|' in line:
        # Remove bullet point if present
        line = line.lstrip('* ').strip()
        parts = line.split('|')
        if len(parts) == 2:
            spanish = parts[0].strip()
            english = parts[1].strip()
            sentences.append((spanish, english))

print(f"Generating audio for {len(sentences)} sentences...")

for idx, (spanish_sentence, english_sentence) in enumerate(tqdm(sentences)):
    # Skip if already exists
    output_file = f"sentence_audio/{idx+1:03d}_sentence.mp3"
    
    if os.path.exists(output_file):
        continue
    
    # Retry logic
    max_retries = 3
    for attempt in range(max_retries):
        try:
            # Generate Spanish audio
            spanish_tts = gTTS(text=spanish_sentence, lang='es', slow=False)
            spanish_file = f"temp_spanish/{idx:03d}.mp3"
            spanish_tts.save(spanish_file)
            
            # Generate English audio
            english_tts = gTTS(text=english_sentence, lang='en', slow=False)
            english_file = f"temp_english/{idx:03d}.mp3"
            english_tts.save(english_file)
            
            # Combine with 1 second pause (longer for sentences)
            subprocess.run([
                'ffmpeg', '-y', '-i', spanish_file, '-i', english_file,
                '-filter_complex', 
                '[0:a][1:a]concat=n=2:v=0:a=1,adelay=1000|1000[out]',
                '-map', '[out]', output_file
            ], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            
            # Clean up
            if os.path.exists(spanish_file):
                os.remove(spanish_file)
            if os.path.exists(english_file):
                os.remove(english_file)
            
            break
            
        except Exception as e:
            print(f"\nError on sentence {idx+1}: {e}")
            if attempt < max_retries - 1:
                print(f"Retrying (attempt {attempt+2}/{max_retries})...")
                time.sleep(2)
            else:
                print(f"Failed after {max_retries} attempts. Skipping...")
    
    time.sleep(0.5)  # Slower for sentences since they take more API calls

print("✅ Done! Cleaning up temp folders...")
os.system('rm -rf temp_spanish temp_english')
print("✅ All done! Check the 'sentence_audio' folder")