# Sahasranamam & Stotra Learning App - Audit Guidelines & Checklist

## 1. Security & Privacy Audit
- [ ] **100% Local Privacy**: User learning progress, review logs, and notes are stored strictly on-device in local Room DB. No unencrypted cloud transmission.
- [ ] **Path Traversal Protection**: Audio and asset file paths must be loaded via validated relative assets without absolute filesystem vulnerability.
- [ ] **SQL Injection Prevention**: All DAO queries use parametrized inputs (`:query`). Parameter wildcards (`%`, `_`) must be escaped before passing to search queries.

## 2. Database & Data Model Integrity Audit
- [ ] **Normalized Entities**: Decoupled `StotraEntity`, `ShlokaEntity`, `PadaEntity`, and `UserSrsProgressEntity`.
- [ ] **Cascading Foreign Keys**: Child records (`shlokas`, `padas`, `user_srs_progress`) automatically clean up when parent stotra is updated/deleted.
- [ ] **Query Performance**: Indexing on `stotra_id`, `shloka_id`, and `next_review_due` to guarantee sub-millisecond query execution.
- [ ] **Thread Safety**: All database reads/writes strictly execute on `Dispatchers.IO` using Kotlin `Flow` and Coroutines.

## 3. Sandhi Viccheda & Audio Engine Audit
- [ ] **Sandhi Splitting Accuracy**: Ensure full verse and *Pada-chheda* (split words) map 1-to-1 without character loss.
- [ ] **Timestamp Audio Sync**: `audio_start_ms` and `audio_end_ms` boundaries must be strictly validated ($start \le end$).
- [ ] **Playback Speed Preservation**: Audio speed adjustments (0.5x, 0.75x, 1x) preserve audio pitch without voice distortion.

## 4. Spaced Repetition (SRS) Engine Audit
- [ ] **SM-2 Compliance**: Correct calculation of Ease Factor ($EF \ge 1.3$) and repetition interval progression.
- [ ] **State Machine Integrity**: Cards move deterministically through `NEW` (0) $\rightarrow$ `LEARNING` (1) $\rightarrow$ `REVIEW` (2) $\rightarrow$ `MASTERED` (3).
