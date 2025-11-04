import pandas as pd
from gtts import gTTS
from tqdm import tqdm
import os
import subprocess
import time

# Create output folders
os.makedirs("temp_spanish", exist_ok=True)
os.makedirs("temp_english", exist_ok=True)
os.makedirs("numbered_sentence_audio", exist_ok=True)

# Read file
df = pd.read_csv("numbered_sentences.txt", sep="\t", 
                 names=["number", "spanish", "english"],
                 on_bad_lines='skip', encoding='utf-8')

print(f"Generating audio for {len(df)} sentences...")

for idx, row in tqdm(df.iterrows(), total=len(df)):
    number = str(row['number']).strip()
    spanish_sentence = str(row['spanish']).strip()
    english_sentence = str(row['english']).strip()
    
    # Skip if already exists
    output_file = f"numbered_sentence_audio/{idx+1:03d}_sentence.mp3"
    
    if os.path.exists(output_file):
        continue
    
    # Retry logic
    max_retries = 3
    for attempt in range(max_retries):
        try:
            # Generate Spanish with number (first time)
            spanish_with_num = gTTS(text=f"{number}. {spanish_sentence}", lang='es', slow=True)
            spanish_num_file = f"temp_spanish/{idx:03d}_num.mp3"
            spanish_with_num.save(spanish_num_file)
            
            # Generate Spanish without number (second time)
            spanish_only = gTTS(text=spanish_sentence, lang='es', slow=False)
            spanish_only_file = f"temp_spanish/{idx:03d}_only.mp3"
            spanish_only.save(spanish_only_file)
            
            # Generate English audio
            english_tts = gTTS(text=english_sentence, lang='en', slow=False)
            english_file = f"temp_english/{idx:03d}.mp3"
            english_tts.save(english_file)
            
            # Combine: Spanish(num) + pause + Spanish(no num) + pause + English
            subprocess.run([
                'ffmpeg', '-y', 
                '-i', spanish_num_file, 
                '-i', spanish_only_file, 
                '-i', english_file,
                '-filter_complex', 
                '[0:a][1:a][2:a]concat=n=3:v=0:a=1,adelay=700|700[out]',
                '-map', '[out]', output_file
            ], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            
            # Clean up
            for f in [spanish_num_file, spanish_only_file, english_file]:
                if os.path.exists(f):
                    os.remove(f)
            
            break
            
        except Exception as e:
            print(f"\nError on sentence {idx+1}: {e}")
            if attempt < max_retries - 1:
                print(f"Retrying (attempt {attempt+2}/{max_retries})...")
                time.sleep(2)
            else:
                print(f"Failed after {max_retries} attempts. Skipping...")
    
    time.sleep(0.5)

print("✅ Done! Cleaning up temp folders...")
os.system('rm -rf temp_spanish temp_english')
print("✅ All done! Check the 'numbered_sentence_audio' folder")