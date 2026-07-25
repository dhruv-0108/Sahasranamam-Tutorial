package com.stotra.sahasranamam.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_srs_progress",
    foreignKeys = [
        ForeignKey(
            entity = ShlokaEntity::class,
            parentColumns = ["id"],
            childColumns = ["shloka_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["shloka_id"], unique = true),
        Index(value = ["next_review_due"])
    ]
)
data class UserSrsProgressEntity(
    @PrimaryKey
    @ColumnInfo(name = "shloka_id")
    val shlokaId: Long,

    @ColumnInfo(name = "state")
    val state: Int = 0, // 0 = New, 1 = Learning, 2 = Review, 3 = Mastered

    @ColumnInfo(name = "repetition_count")
    val repetitionCount: Int = 0,

    @ColumnInfo(name = "interval_days")
    val intervalDays: Double = 0.0,

    @ColumnInfo(name = "ease_factor")
    val easeFactor: Double = 2.5,

    @ColumnInfo(name = "last_reviewed_at")
    val lastReviewedAt: Long = 0,

    @ColumnInfo(name = "next_review_due")
    val nextReviewDue: Long = 0
)
