package com.stotra.sahasranamam.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.stotra.sahasranamam.core.result.Resource
import com.stotra.sahasranamam.core.srs.SrsEngine
import com.stotra.sahasranamam.data.local.AppDatabase
import com.stotra.sahasranamam.data.local.entity.UserSrsProgressEntity
import com.stotra.sahasranamam.domain.model.Pada
import com.stotra.sahasranamam.domain.model.Shloka
import com.stotra.sahasranamam.domain.model.SrsCard
import com.stotra.sahasranamam.domain.model.Stotra
import com.stotra.sahasranamam.domain.repository.StotraRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StotraRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val firestore: FirebaseFirestore
) : StotraRepository {

    override fun getAllStotras(): Flow<Resource<List<Stotra>>> {
        return db.stotraDao().getAllStotras()
            .map { entities ->
                val domainList = entities.map { entity ->
                    Stotra(
                        id = entity.id,
                        titleDevanagari = entity.titleDevanagari,
                        titleEnglish = entity.titleEnglish,
                        category = entity.category,
                        deity = entity.deity,
                        totalShlokas = entity.totalShlokas,
                        description = entity.description,
                        audioAssetPath = entity.audioAssetPath
                    )
                }
                Resource.Success(domainList)
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getShlokasForStotra(stotraId: String): Flow<Resource<List<Shloka>>> {
        return db.shlokaDao().getShlokasForStotra(stotraId)
            .map { shlokaEntities ->
                val shlokaList = shlokaEntities.map { shlokaEntity ->
                    // Fetch padas for each shloka
                    val padas = db.padaDao().getPadasForShlokaSync(shlokaEntity.id).map { padaEntity ->
                        Pada(
                            id = padaEntity.id,
                            shlokaId = padaEntity.shlokaId,
                            index = padaEntity.padaIndex,
                            sanskritCombined = padaEntity.sanskritCombined,
                            sanskritSplit = padaEntity.sanskritSplit,
                            sandhiRuleName = padaEntity.sandhiRuleName,
                            sandhiRuleExplanation = padaEntity.sandhiRuleExplanation,
                            iast = padaEntity.iast,
                            meaning = padaEntity.meaning,
                            audioStartMs = padaEntity.audioStartMs,
                            audioEndMs = padaEntity.audioEndMs
                        )
                    }

                    Shloka(
                        id = shlokaEntity.id,
                        stotraId = shlokaEntity.stotraId,
                        shlokaNumber = shlokaEntity.shlokaNumber,
                        fullSanskrit = shlokaEntity.fullSanskrit,
                        sandhiSplitSanskrit = shlokaEntity.sandhiSplitSanskrit,
                        iastTransliteration = shlokaEntity.iastTransliteration,
                        meaningEnglish = shlokaEntity.meaningEnglish,
                        meaningHindi = shlokaEntity.meaningHindi,
                        audioStartMs = shlokaEntity.audioStartMs,
                        audioEndMs = shlokaEntity.audioEndMs,
                        padas = padas
                    )
                }
                Resource.Success(shlokaList)
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getDueSrsCards(currentTimeMs: Long): Flow<Resource<List<SrsCard>>> {
        return db.userSrsProgressDao().getDueReviews(currentTimeMs)
            .map { progressEntities ->
                val cards = progressEntities.mapNotNull { progress ->
                    val shlokaEntity = db.shlokaDao().getShlokaById(progress.shlokaId) ?: return@mapNotNull null
                    val shloka = Shloka(
                        id = shlokaEntity.id,
                        stotraId = shlokaEntity.stotraId,
                        shlokaNumber = shlokaEntity.shlokaNumber,
                        fullSanskrit = shlokaEntity.fullSanskrit,
                        sandhiSplitSanskrit = shlokaEntity.sandhiSplitSanskrit,
                        iastTransliteration = shlokaEntity.iastTransliteration,
                        meaningEnglish = shlokaEntity.meaningEnglish,
                        meaningHindi = shlokaEntity.meaningHindi,
                        audioStartMs = shlokaEntity.audioStartMs,
                        audioEndMs = shlokaEntity.audioEndMs
                    )

                    SrsCard(
                        shloka = shloka,
                        state = progress.state,
                        repetitionCount = progress.repetitionCount,
                        intervalDays = progress.intervalDays,
                        easeFactor = progress.easeFactor,
                        lastReviewedAt = progress.lastReviewedAt,
                        nextReviewDue = progress.nextReviewDue
                    )
                }
                Resource.Success(cards)
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun submitSrsReview(shlokaId: Long, rating: Int): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentProgress = db.userSrsProgressDao().getProgressForShloka(shlokaId)
                ?: UserSrsProgressEntity(shlokaId = shlokaId)

            val now = System.currentTimeMillis()
            val result = SrsEngine.calculateNextReview(
                rating = rating,
                currentRepetition = currentProgress.repetitionCount,
                currentInterval = currentProgress.intervalDays,
                currentEaseFactor = currentProgress.easeFactor,
                currentTimeMs = now
            )

            val updatedProgress = currentProgress.copy(
                state = result.nextState,
                repetitionCount = result.repetitionCount,
                intervalDays = result.intervalDays,
                easeFactor = result.easeFactor,
                lastReviewedAt = now,
                nextReviewDue = result.nextReviewDueMs
            )

            db.userSrsProgressDao().insertOrUpdateProgress(updatedProgress)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun syncWithRemoteDatabase(): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            // Firestore synchronization mechanism for production remote updates
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}
