package com.gnomeshift.recommendationservice.dto

import kotlinx.serialization.Serializable

@Serializable
data class RatingEvent(
    val userId: Long,
    val courseId: Long,
    val rating: Int,
    val eventType: String = "RATING_CREATED",
    val timestamp: Long
)
