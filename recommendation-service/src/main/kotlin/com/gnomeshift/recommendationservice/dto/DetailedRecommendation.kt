package com.gnomeshift.recommendationservice.dto

import java.time.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class DetailedRecommendation(
    val courseId: Long,
    val score: Double,
    val reason: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime
)
