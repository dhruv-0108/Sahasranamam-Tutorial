package com.stotra.sahasranamam.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shlokas",
    foreignKeys = [
        ForeignKey(
            entity = StotraEntity::class,
            parentColumns = ["id"],
            childColumns = ["stotra_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["stotra_id"]),
        Index(value = ["stotra_id", "shloka_number"])
    ]
)
data class ShlokaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "stotra_id")
    val stotraId: String,

    @ColumnInfo(name = "shloka_number")
    val shlokaNumber: Int,

    @ColumnInfo(name = "full_sanskrit")
    val fullSanskrit: String, // Full concatenated Sanskrit shloka

    @ColumnInfo(name = "sandhi_split_sanskrit")
    val sandhiSplitSanskrit: String, // Split Pada-chheda verse

    @ColumnInfo(name = "iast_transliteration")
    val iastTransliteration: String,

    @ColumnInfo(name = "meaning_english")
    val meaningEnglish: String,

    @ColumnInfo(name = "meaning_hindi")
    val meaningHindi: String? = null,

    @ColumnInfo(name = "audio_start_ms")
    val audioStartMs: Long = 0,

    @ColumnInfo(name = "audio_end_ms")
    val audioEndMs: Long = 0
)
