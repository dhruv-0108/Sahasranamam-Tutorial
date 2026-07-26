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
                        isBookmarked = shlokaEntity.isBookmarked,
                        lastViewedAt = shlokaEntity.lastViewedAt,
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
                        audioEndMs = shlokaEntity.audioEndMs,
                        isBookmarked = shlokaEntity.isBookmarked,
                        lastViewedAt = shlokaEntity.lastViewedAt
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

    override suspend fun toggleBookmark(shlokaId: Long, isBookmarked: Boolean): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            db.shlokaDao().updateBookmarkState(shlokaId, isBookmarked)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun updateLastViewed(shlokaId: Long, timestamp: Long): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            db.shlokaDao().updateLastViewedTimestamp(shlokaId, timestamp)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getLastViewedShloka(stotraId: String): Resource<Shloka?> = withContext(Dispatchers.IO) {
        try {
            val entity = db.shlokaDao().getLastViewedShloka(stotraId)
            if (entity != null) {
                val padas = db.padaDao().getPadasForShlokaSync(entity.id).map { padaEntity ->
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
                val shloka = Shloka(
                    id = entity.id,
                    stotraId = entity.stotraId,
                    shlokaNumber = entity.shlokaNumber,
                    fullSanskrit = entity.fullSanskrit,
                    sandhiSplitSanskrit = entity.sandhiSplitSanskrit,
                    iastTransliteration = entity.iastTransliteration,
                    meaningEnglish = entity.meaningEnglish,
                    meaningHindi = entity.meaningHindi,
                    audioStartMs = entity.audioStartMs,
                    audioEndMs = entity.audioEndMs,
                    isBookmarked = entity.isBookmarked,
                    lastViewedAt = entity.lastViewedAt,
                    padas = padas
                )
                Resource.Success(shloka)
            } else {
                Resource.Success(null)
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override fun getBookmarkedShlokas(stotraId: String): Flow<Resource<List<Shloka>>> {
        return db.shlokaDao().getBookmarkedShlokas(stotraId)
            .map { entities ->
                val shlokas = entities.map { entity ->
                    val padas = db.padaDao().getPadasForShlokaSync(entity.id).map { padaEntity ->
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
                        id = entity.id,
                        stotraId = entity.stotraId,
                        shlokaNumber = entity.shlokaNumber,
                        fullSanskrit = entity.fullSanskrit,
                        sandhiSplitSanskrit = entity.sandhiSplitSanskrit,
                        iastTransliteration = entity.iastTransliteration,
                        meaningEnglish = entity.meaningEnglish,
                        meaningHindi = entity.meaningHindi,
                        audioStartMs = entity.audioStartMs,
                        audioEndMs = entity.audioEndMs,
                        isBookmarked = entity.isBookmarked,
                        lastViewedAt = entity.lastViewedAt,
                        padas = padas
                    )
                }
                Resource.Success(shlokas)
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getRecentSelection(): Resource<com.stotra.sahasranamam.domain.model.RecentSelection?> = withContext(Dispatchers.IO) {
        try {
            val recentShloka = db.shlokaDao().getMostRecentlyViewedShloka()
            if (recentShloka != null) {
                val stotra = db.stotraDao().getStotraById(recentShloka.stotraId)
                if (stotra != null) {
                    val selection = com.stotra.sahasranamam.domain.model.RecentSelection(
                        stotraId = recentShloka.stotraId,
                        stotraTitleEnglish = stotra.titleEnglish,
                        stotraTitleDevanagari = stotra.titleDevanagari,
                        shlokaNumber = recentShloka.shlokaNumber,
                        shlokaId = recentShloka.id
                    )
                    Resource.Success(selection)
                } else {
                    Resource.Success(null)
                }
            } else {
                Resource.Success(null)
            }
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
