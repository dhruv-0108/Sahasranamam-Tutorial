# Guide: How to Add New Stotras or Suktams

To add any new stotram or suktam to the application under the same strict, high-quality standard as Vishnu Sahasranamam and Aditya Hrudayam, please follow this guide.

---

## 1. What We Need From You
To import a new stotra, please provide:
1. **The Full Text**: In Devanagari Sanskrit or clean IAST transliteration (e.g., standard text file or PDF).
2. **Translation**: The overall English meaning for each verse (and Hindi meaning if desired).
3. **Audio File (Optional)**: If you have a recorded chanting audio file (MP3 or WAV), we can sync the timings. If no audio file is provided, the app will automatically fall back to high-quality Text-To-Speech (TTS) for both the full verse and individual words!
4. **Word-by-word Meanings (Optional)**: If you have a word-by-word dictionary, that is excellent. If not, we can translate each word and resolve the sandhi splits using our Sanskrit capabilities.

---

## 2. JSON Structure Requirements

Every stotra is added as a JSON file in `app/src/main/assets/stotras/<stotra_id>.json`. The file must strictly follow this structure:

```json
{
  "stotra": {
    "id": "sri_suktam",
    "title_devanagari": "श्रीसूक्तम्",
    "title_english": "Sri Suktam",
    "category": "Suktams",
    "deity": "Goddess Mahalakshmi",
    "total_shlokas": 16,
    "description": "Short description of the stotra.",
    "audio_asset_path": null
  },
  "shlokas": [
    {
      "shloka_number": 1,
      "full_sanskrit": "हिरण्यवर्णां हरिणीं सुवर्णरजतस्रजाम् ।\nचन्द्रां हिरण्मयीं लक्ष्मीं जातवेदो म आवह ॥",
      "sandhi_split_sanskrit": "हिरण्य - वर्णाम् | हरिणीम् | सुवर्ण - रजत - स्रजाम् |\nचन्द्राम् | हिरण्मयीम् | लक्ष्मीम् | जातवेदः | मे | आवह ||",
      "iast_transliteration": "hiraṇyavarṇāṁ hariṇīṁ suvarṇarajatasrajām |\ncandrāṁ hiraṇmayīṁ lakṣmīṁ jātavedo ma āvaha ||",
      "meaning_english": "O Agni! Invoke for me Goddess Lakshmi...",
      "meaning_hindi": "हे अग्निदेव! मेरे लिए लक्ष्मी का आवाह्न करें...",
      "audio_start_ms": 0,
      "audio_end_ms": 13200,
      "padas": [
        {
          "pada_index": 1,
          "sanskrit_combined": "हिरण्यवर्णां",
          "sanskrit_split": "हिरण्य + वर्णाम्",
          "sandhi_rule_name": null,
          "sandhi_rule_explanation": null,
          "iast": "hiraṇyavarṇām",
          "meaning": "golden-colored",
          "audio_start_ms": 0,
          "audio_end_ms": 2300
        },
        {
          "pada_index": 2,
          "sanskrit_combined": "हरिणीं",
          "sanskrit_split": "हरिणीम्",
          "sandhi_rule_name": null,
          "sandhi_rule_explanation": null,
          "iast": "hariṇīm",
          "meaning": "radiant / like a deer",
          "audio_start_ms": 2300,
          "audio_end_ms": 3800
        }
      ]
    }
  ]
}
```

### Key Formatting Rules:
1. **`sanskrit_combined`**: Must contain the exact Sanskrit word as it is written in the verse.
2. **`sanskrit_split`**: If the word is a compound (e.g. `हिरण्यवर्णां`), show the split with a `+` sign (e.g. `हिरण्य + वर्णाम्`). If the word has no sandhi split, set it to the same as `sanskrit_combined`.
3. **`meaning`**: Must contain the real, contextual English dictionary meaning of that specific word.
4. **Categories**: The category field must match one of the standard supported classifications exactly:
   * `"Sahasranamam"`
   * `"Stotras"`
   * `"Suktams"`
   * `"Ashtakams"`
   * `"Chalisa"`
   * `"Namvali"`

---

## 3. How We Import It
Once you provide the materials, we:
1. Parse the text and split the verses.
2. Translate each verse and construct word-by-word definitions and sandhis.
3. Save the JSON in the assets directory.
4. Register the new JSON path in `DatabaseInitializer.kt`.
5. Bump the database version to force seeding.
