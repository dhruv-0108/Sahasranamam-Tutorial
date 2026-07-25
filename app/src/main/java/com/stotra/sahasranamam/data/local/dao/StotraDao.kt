package com.stotra.sahasranamam.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stotra.sahasranamam.data.local.entity.StotraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StotraDao {

    @Query("SELECT * FROM stotras ORDER BY title_english ASC")
    fun getAllStotras(): Flow<List<StotraEntity>>

    @Query("SELECT * FROM stotras WHERE category = :category ORDER BY title_english ASC")
    fun getStotrasByCategory(category: String): Flow<List<StotraEntity>>

    @Query("SELECT * FROM stotras WHERE id = :stotraId")
    suspend fun getStotraById(stotraId: String): StotraEntity?

    @Query("SELECT * FROM stotras WHERE title_devanagari LIKE '%' || :query || '%' OR title_english LIKE '%' || :query || '%'")
    fun searchStotras(query: String): Flow<List<StotraEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStotra(stotra: StotraEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStotras(stotras: List<StotraEntity>)
}
