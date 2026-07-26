package com.stotra.sahasranamam.data.local.initializer

import android.content.Context
import com.google.gson.Gson
import com.stotra.sahasranamam.core.security.SecuritySanitizer
import com.stotra.sahasranamam.data.local.AppDatabase
import com.stotra.sahasranamam.data.local.entity.PadaEntity
import com.stotra.sahasranamam.data.local.entity.ShlokaEntity
import com.stotra.sahasranamam.data.local.entity.StotraEntity
import com.stotra.sahasranamam.data.local.entity.UserSrsProgressEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Parses asset JSON files and pre-populates Room database on app first launch.
 */
class DatabaseInitializer(
    private val context: Context,
    private val db: AppDatabase,
    private val gson: Gson = Gson()
) {

    private val assetFiles = listOf(
        "stotras/aditya_hrudayam.json",
        "stotras/sri_suktam.json",
        "stotras/vishnu_sahasranamam.json"
    )

    suspend fun seedDatabaseIfNeeded() = withContext(Dispatchers.IO) {
        val existingStotras = db.stotraDao().getStotraById("aditya_hrudayam")
        if (existingStotras != null) {
            // Already seeded
            return@withContext
        }

        assetFiles.forEach { relativePath ->
            if (SecuritySanitizer.isSafeAssetPath(relativePath)) {
                val jsonString = context.assets.open(relativePath).bufferedReader().use { it.readText() }
                val parsed = gson.fromJson(jsonString, RawStotraJson::class.java)

                if (parsed?.stotra != null) {
                    val stotraEntity = StotraEntity(
                        id = parsed.stotra.id,
                        titleDevanagari = parsed.stotra.title_devanagari,
                        titleEnglish = parsed.stotra.title_english,
                        category = parsed.stotra.category,
                        deity = parsed.stotra.deity,
                        totalShlokas = parsed.stotra.total_shlokas,
                        description = parsed.stotra.description,
                        audioAssetPath = parsed.stotra.audio_asset_path
                    )
                    db.stotraDao().insertStotra(stotraEntity)

                    parsed.shlokas.forEach { rawShloka ->
                        val shlokaEntity = ShlokaEntity(
                            stotraId = parsed.stotra.id,
                            shlokaNumber = rawShloka.shloka_number,
                            fullSanskrit = rawShloka.full_sanskrit,
                            sandhiSplitSanskrit = rawShloka.sandhi_split_sanskrit,
                            iastTransliteration = rawShloka.iast_transliteration,
                            meaningEnglish = rawShloka.meaning_english,
                            meaningHindi = rawShloka.meaning_hindi,
                            audioStartMs = rawShloka.audio_start_ms,
                            audioEndMs = rawShloka.audio_end_ms
                        )
                        val shlokaIds = db.shlokaDao().insertShlokas(listOf(shlokaEntity))
                        val generatedShlokaId = shlokaIds.firstOrNull() ?: return@forEach

                        // Initialize user SRS progress record
                        val srsProgress = UserSrsProgressEntity(
                            shlokaId = generatedShlokaId,
                            state = 0, // NEW
                            repetitionCount = 0,
                            intervalDays = 0.0,
                            easeFactor = 2.5,
                            lastReviewedAt = 0,
                            nextReviewDue = System.currentTimeMillis()
                        )
                        db.userSrsProgressDao().insertOrUpdateProgress(srsProgress)

                        // Insert padas
                        val padaEntities = rawShloka.padas.map { rawPada ->
                            PadaEntity(
                                shlokaId = generatedShlokaId,
                                padaIndex = rawPada.pada_index,
                                sanskritCombined = rawPada.sanskrit_combined,
                                sanskritSplit = rawPada.sanskrit_split,
                                sandhiRuleName = rawPada.sandhi_rule_name,
                                sandhiRuleExplanation = rawPada.sandhi_rule_explanation,
                                iast = rawPada.iast,
                                meaning = rawPada.meaning,
                                audioStartMs = rawPada.audio_start_ms,
                                audioEndMs = rawPada.audio_end_ms
                            )
                        }
                        if (padaEntities.isNotEmpty()) {
                            db.padaDao().insertPadas(padaEntities)
                        }
                    }
                }
            }
        }
    }

    private data class RawStotraJson(
        val stotra: RawStotraHeader,
        val shlokas: List<RawShlokaItem>
    )

    private data class RawStotraHeader(
        val id: String,
        val title_devanagari: String,
        val title_english: String,
        val category: String,
        val deity: String,
        val total_shlokas: Int,
        val description: String,
        val audio_asset_path: String?
    )

    private data class RawShlokaItem(
        val shloka_number: Int,
        val full_sanskrit: String,
        val sandhi_split_sanskrit: String,
        val iast_transliteration: String,
        val meaning_english: String,
        val meaning_hindi: String?,
        val audio_start_ms: Long,
        val audio_end_ms: Long,
        val padas: List<RawPadaItem> = emptyList()
    )

    private data class RawPadaItem(
        val pada_index: Int,
        val sanskrit_combined: String,
        val sanskrit_split: String,
        val sandhi_rule_name: String?,
        val sandhi_rule_explanation: String?,
        val iast: String,
        val meaning: String,
        val audio_start_ms: Long,
        val audio_end_ms: Long
    )
}
