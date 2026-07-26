package com.stotra.sahasranamam.domain.model

data class Shloka(
    val id: Long,
    val stotraId: String,
    val shlokaNumber: Int,
    val fullSanskrit: String,
    val sandhiSplitSanskrit: String,
    val iastTransliteration: String,
    val meaningEnglish: String,
    val meaningHindi: String?,
    val audioStartMs: Long,
    val audioEndMs: Long,
    val isBookmarked: Boolean = false,
    val lastViewedAt: Long = 0,
    val padas: List<Pada> = emptyList()
)
