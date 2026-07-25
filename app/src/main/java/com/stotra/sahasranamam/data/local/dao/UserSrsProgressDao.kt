package com.stotra.sahasranamam.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stotra.sahasranamam.data.local.entity.UserSrsProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSrsProgressDao {

    @Query("SELECT * FROM user_srs_progress WHERE shloka_id = :shlokaId")
    suspend fun getProgressForShloka(shlokaId: Long): UserSrsProgressEntity?

    @Query("SELECT * FROM user_srs_progress WHERE next_review_due <= :currentTimeMs ORDER BY next_review_due ASC")
    fun getDueReviews(currentTimeMs: Long): Flow<List<UserSrsProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: UserSrsProgressEntity)
}
