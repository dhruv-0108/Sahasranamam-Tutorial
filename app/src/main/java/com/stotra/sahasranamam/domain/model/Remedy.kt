package com.stotra.sahasranamam.domain.model

data class RemedyCategory(
    val categoryId: String,
    val titleEnglish: String,
    val titleHindi: String,
    val descriptionEnglish: String,
    val descriptionHindi: String,
    val recommendations: List<Recommendation>
)

data class Recommendation(
    val stotraId: String,
    val reasonEnglish: String,
    val reasonHindi: String
)
