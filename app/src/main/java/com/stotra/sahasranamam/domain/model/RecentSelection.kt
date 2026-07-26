package com.stotra.sahasranamam.domain.model

data class RecentSelection(
    val stotraId: String,
    val stotraTitleEnglish: String,
    val stotraTitleDevanagari: String,
    val shlokaNumber: Int,
    val shlokaId: Long
)
