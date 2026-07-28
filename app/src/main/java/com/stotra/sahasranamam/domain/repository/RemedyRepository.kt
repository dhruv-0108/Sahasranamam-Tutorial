package com.stotra.sahasranamam.domain.repository

import com.stotra.sahasranamam.core.result.Resource
import com.stotra.sahasranamam.domain.model.RemedyCategory
import kotlinx.coroutines.flow.Flow

interface RemedyRepository {
    fun getRemedyCategories(): Flow<Resource<List<RemedyCategory>>>
}
