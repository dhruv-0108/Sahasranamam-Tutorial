package com.stotra.sahasranamam.domain.model

data class Stotra(
    val id: String,
    val titleDevanagari: String,
    val titleEnglish: String,
    val category: String,
    val deity: String,
    val totalShlokas: Int,
    val description: String,
    val audioAssetPath: String?
)
