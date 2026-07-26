package com.stotra.sahasranamam.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stotra.sahasranamam.data.local.entity.ShlokaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShlokaDao {

    @Query("SELECT * FROM shlokas WHERE stotra_id = :stotraId ORDER BY shloka_number ASC")
    fun getShlokasForStotra(stotraId: String): Flow<List<ShlokaEntity>>

    @Query("SELECT * FROM shlokas WHERE id = :shlokaId")
    suspend fun getShlokaById(shlokaId: Long): ShlokaEntity?

    @Query("SELECT * FROM shlokas WHERE stotra_id = :stotraId AND shloka_number = :shlokaNumber")
    suspend fun getShlokaByNumber(stotraId: String, shlokaNumber: Int): ShlokaEntity?

    @Query("UPDATE shlokas SET is_bookmarked = :isBookmarked WHERE id = :shlokaId")
    suspend fun updateBookmarkState(shlokaId: Long, isBookmarked: Boolean)

    @Query("UPDATE shlokas SET last_viewed_at = :timestamp WHERE id = :shlokaId")
    suspend fun updateLastViewedTimestamp(shlokaId: Long, timestamp: Long)

    @Query("SELECT * FROM shlokas WHERE stotra_id = :stotraId AND is_bookmarked = 1 ORDER BY shloka_number ASC")
    fun getBookmarkedShlokas(stotraId: String): Flow<List<ShlokaEntity>>

    @Query("SELECT * FROM shlokas WHERE stotra_id = :stotraId AND last_viewed_at > 0 ORDER BY last_viewed_at DESC LIMIT 1")
    suspend fun getLastViewedShloka(stotraId: String): ShlokaEntity?

    @Query("SELECT * FROM shlokas WHERE last_viewed_at > 0 ORDER BY last_viewed_at DESC LIMIT 1")
    suspend fun getMostRecentlyViewedShloka(): ShlokaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShlokas(shlokas: List<ShlokaEntity>): List<Long>
}
