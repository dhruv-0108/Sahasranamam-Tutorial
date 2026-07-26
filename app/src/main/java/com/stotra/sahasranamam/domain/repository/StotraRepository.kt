package com.stotra.sahasranamam.domain.repository

import com.stotra.sahasranamam.core.result.Resource
import com.stotra.sahasranamam.domain.model.Shloka
import com.stotra.sahasranamam.domain.model.SrsCard
import com.stotra.sahasranamam.domain.model.Stotra
import kotlinx.coroutines.flow.Flow

interface StotraRepository {

    /**
     * Retrieves all Stotras from the database.
     */
    fun getAllStotras(): Flow<Resource<List<Stotra>>>

    /**
     * Retrieves all Shlokas for a specific Stotra with Sandhi splits and Padas.
     */
    fun getShlokasForStotra(stotraId: String): Flow<Resource<List<Shloka>>>

    /**
     * Retrieves due flashcards for SRS review.
     */
    fun getDueSrsCards(currentTimeMs: Long): Flow<Resource<List<SrsCard>>>

    /**
     * Submits SRS review rating (1 = Again, 2 = Hard, 3 = Good, 4 = Easy)
     * and updates database state.
     */
    suspend fun submitSrsReview(shlokaId: Long, rating: Int): Resource<Unit>

    /**
     * Toggles bookmark state for a shloka.
     */
    suspend fun toggleBookmark(shlokaId: Long, isBookmarked: Boolean): Resource<Unit>

    /**
     * Updates the last viewed timestamp for a shloka.
     */
    suspend fun updateLastViewed(shlokaId: Long, timestamp: Long): Resource<Unit>

    /**
     * Gets the last viewed shloka for a stotra, if any.
     */
    suspend fun getLastViewedShloka(stotraId: String): Resource<Shloka?>

    /**
     * Gets all bookmarked shlokas for a stotra.
     */
    fun getBookmarkedShlokas(stotraId: String): Flow<Resource<List<Shloka>>>

    /**
     * Gets the most recently viewed shloka details across all stotras.
     */
    suspend fun getRecentSelection(): Resource<com.stotra.sahasranamam.domain.model.RecentSelection?>

    /**
     * Synchronizes local database with remote Cloud Firestore.
     */
    suspend fun syncWithRemoteDatabase(): Resource<Unit>
}
