package com.stotra.sahasranamam.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stotras")
data class StotraEntity(
    @PrimaryKey
    val id: String, // e.g. "vishnu_sahasranamam"

    @ColumnInfo(name = "title_devanagari")
    val titleDevanagari: String,

    @ColumnInfo(name = "title_english")
    val titleEnglish: String,

    @ColumnInfo(name = "category")
    val category: String, // "Sahasranama", "Stotra", "Ashtakam"

    @ColumnInfo(name = "deity")
    val deity: String,

    @ColumnInfo(name = "total_shlokas")
    val totalShlokas: Int,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "audio_asset_path")
    val audioAssetPath: String?
)
