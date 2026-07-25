package com.stotra.sahasranamam.domain.model

data class Pada(
    val id: Long,
    val shlokaId: Long,
    val index: Int,
    val sanskritCombined: String,
    val sanskritSplit: String,
    val sandhiRuleName: String?,
    val sandhiRuleExplanation: String?,
    val iast: String,
    val meaning: String,
    val audioStartMs: Long,
    val audioEndMs: Long
)
