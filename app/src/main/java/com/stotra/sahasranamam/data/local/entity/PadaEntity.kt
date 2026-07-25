package com.stotra.sahasranamam.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "padas",
    foreignKeys = [
        ForeignKey(
            entity = ShlokaEntity::class,
            parentColumns = ["id"],
            childColumns = ["shloka_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["shloka_id"])
    ]
)
data class PadaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "shloka_id")
    val shlokaId: Long,

    @ColumnInfo(name = "pada_index")
    val padaIndex: Int,

    @ColumnInfo(name = "sanskrit_combined")
    val sanskritCombined: String, // e.g. "शुक्लाम्बरधरं"

    @ColumnInfo(name = "sanskrit_split")
    val sanskritSplit: String, // e.g. "शुक्ल + अम्बर + धरम्"

    @ColumnInfo(name = "sandhi_rule_name")
    val sandhiRuleName: String? = null, // e.g. "Dirgha Svara Sandhi"

    @ColumnInfo(name = "sandhi_rule_explanation")
    val sandhiRuleExplanation: String? = null,

    @ColumnInfo(name = "iast")
    val iast: String,

    @ColumnInfo(name = "meaning")
    val meaning: String,

    @ColumnInfo(name = "audio_start_ms")
    val audioStartMs: Long = 0,

    @ColumnInfo(name = "audio_end_ms")
    val audioEndMs: Long = 0
)
