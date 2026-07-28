package com.stotra.sahasranamam.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stotra.sahasranamam.core.result.Resource
import com.stotra.sahasranamam.domain.model.Recommendation
import com.stotra.sahasranamam.domain.model.RemedyCategory
import com.stotra.sahasranamam.domain.repository.RemedyRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemedyRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : RemedyRepository {

    private data class RemedyCategoryDto(
        val category_id: String,
        val title_english: String,
        val title_hindi: String,
        val description_english: String,
        val description_hindi: String,
        val recommendations: List<RecommendationDto>
    )

    private data class RecommendationDto(
        val stotra_id: String,
        val reason_english: String,
        val reason_hindi: String
    )

    override fun getRemedyCategories(): Flow<Resource<List<RemedyCategory>>> = flow {
        emit(Resource.Loading)
        try {
            val jsonString = context.assets.open("remedies.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<RemedyCategoryDto>>() {}.type
            val dtos: List<RemedyCategoryDto> = gson.fromJson(jsonString, type)
            
            val domainList = dtos.map { dto ->
                RemedyCategory(
                    categoryId = dto.category_id,
                    titleEnglish = dto.title_english,
                    titleHindi = dto.title_hindi,
                    descriptionEnglish = dto.description_english,
                    descriptionHindi = dto.description_hindi,
                    recommendations = dto.recommendations.map { recDto ->
                        Recommendation(
                            stotraId = recDto.stotra_id,
                            reasonEnglish = recDto.reason_english,
                            reasonHindi = recDto.reason_hindi
                        )
                    }
                )
            }
            emit(Resource.Success(domainList))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }.flowOn(Dispatchers.IO)
}
