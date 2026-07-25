package com.stotra.sahasranamam.domain.model

data class SrsCard(
    val shloka: Shloka,
    val state: Int,
    val repetitionCount: Int,
    val intervalDays: Double,
    val easeFactor: Double,
    val lastReviewedAt: Long,
    val nextReviewDue: Long
)
