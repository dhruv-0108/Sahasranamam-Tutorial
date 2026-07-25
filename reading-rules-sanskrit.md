# Sanskrit Reading & Recitation Rules (संस्कृत पठन एवं उच्चारण नियमावली)

## 1. Primary Rule of Recitation (संहिता पाठ नियम)
> **Mandatory Rule**: In full verse recitation (मन्त्र पाठ), words MUST NEVER be pronounced in isolated Sandhi-split ( पदच्छेद ) form. Recitation strictly follows **Samhita Patha (संहिता पाठ)** where euphonic junctions and natural flow (सन्धियुक्त पठन) are preserved without artificial breaks.

---

## 2. Distinction Between Display and Recitation

| Aspect | Continuous Recitation (संहिता) | Word-by-Word Study (पदच्छेद) |
| :--- | :--- | :--- |
| **Purpose** | Devotional chanting, authentic Vedic & Stotra audio | Academic learning, grammar understanding, vocabulary breakdown |
| **Audio Treatment** | Unbroken flow preserving Visarga, Anusvara & Sandhi transitions | Individual word audio clips for isolated pronunciation practice |
| **Example** | `जातवेदो म आवह` (*jātavedo ma āvaha*) | `जातवेदः | मे | आवह` |

---

## 3. Pronunciation & Phonetic Rules (उच्चारण सम्बन्धी नियम)

### A. Anusvara (अनुस्वार) Pronunciation
- **Before Stops**: Anusvara takes the nasal sound of the following consonant class:
  - Before \(k, kh, g, gh\): \(\dot{m} \rightarrow \dot{n}\) (e.g. `गङ्गा`)
  - Before \(c, ch, j, jh\): \(\dot{m} \rightarrow \tilde{n}\) (e.g. `ञ्च`)
  - Before \(t, th, d, dh\): \(\dot{m} \rightarrow n\)
  - Before \(p, ph, b, bh\): \(\dot{m} \rightarrow m\)
- **At End of Line**: Pronounced as clear labial nasal `-m` (`म ्`).

### B. Visarga (विसर्ग) Pronunciation
- Visarga (`ः`) reflects the preceding vowel echo softly:
  - After `a` (`अः`): `-aha`
  - After `i` (`इः`): `-ihi`
  - After `u` (`उः`): `-uhu`
  - After `e` (`एः`): `-ehe`
  - After `ai` (`ऐः`): `-aihi`
  - After `o` (`ओः`): `-oho`

### C. Syllable Weight & Meter (छन्दो नियम)
- **Laghu (लघु - Short)**: Single short vowel (\(a, i, u, ṛ\)).
- **Guru (गुरु - Long)**: Long vowel (\(ā, ī, ū, e, ai, o, au\)) OR any short vowel followed by Conjunct/Visarga/Anusvara.

---

## 4. Audio Engine Guidelines for App Implementation
1. **Full Shloka Playback**: Audio track must read Samhita Sanskrit text continuously (`fullSanskrit`).
2. **Individual Pada Playback**: When tapping an individual Pada card, the audio engine isolates and pronounces that specific word (`pada.sanskritSplit` or `pada.sanskritCombined`).
