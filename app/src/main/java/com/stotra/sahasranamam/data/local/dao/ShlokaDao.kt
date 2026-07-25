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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShlokas(shlokas: List<ShlokaEntity>): List<Long>
}
