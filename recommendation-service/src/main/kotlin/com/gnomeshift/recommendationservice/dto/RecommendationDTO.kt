package com.gnomeshift.recommendationservice.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecommendationDTO(
    val courseId: Long,
    val score: Double,
    val reason: String? = null
)
