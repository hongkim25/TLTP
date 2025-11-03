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
    
    # Generate Spanish audio
    spanish_tts = gTTS(text=spanish_word, lang='es', slow=False)
    spanish_file = f"temp_spanish/{idx:04d}.mp3"
    spanish_tts.save(spanish_file)
    
    # Generate English audio
    english_tts = gTTS(text=english_meaning, lang='en', slow=False)
    english_file = f"temp_english/{idx:04d}.mp3"
    english_tts.save(english_file)
    
    # Combine using ffmpeg with 0.5 second silence
    safe_filename = spanish_word.replace(' ', '_').replace('/', '_')[:50]
    output_file = f"audio_files/{idx+1:04d}_{safe_filename}.mp3"
    
    # ffmpeg command to concatenate with silence
    subprocess.run([
        'ffmpeg', '-y', '-i', spanish_file, '-i', english_file,
        '-filter_complex', 
        '[0:a][1:a]concat=n=2:v=0:a=1,adelay=500|500[out]',
        '-map', '[out]', output_file
    ], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    
    time.sleep(0.1)

print("✅ Done! Cleaning up temp files...")
os.system('rm -rf temp_spanish temp_english')
print("✅ All done! Check the 'audio_files' folder")