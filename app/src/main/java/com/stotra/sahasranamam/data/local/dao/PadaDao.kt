package com.stotra.sahasranamam.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stotra.sahasranamam.data.local.entity.PadaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PadaDao {

    @Query("SELECT * FROM padas WHERE shloka_id = :shlokaId ORDER BY pada_index ASC")
    fun getPadasForShloka(shlokaId: Long): Flow<List<PadaEntity>>

    @Query("SELECT * FROM padas WHERE shloka_id = :shlokaId ORDER BY pada_index ASC")
    suspend fun getPadasForShlokaSync(shlokaId: Long): List<PadaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPadas(padas: List<PadaEntity>)
}
