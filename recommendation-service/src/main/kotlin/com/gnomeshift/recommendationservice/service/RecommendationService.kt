package com.gnomeshift.recommendationservice.service

import com.gnomeshift.recommendationservice.database.CourseCategories
import com.gnomeshift.recommendationservice.database.Recommendations
import com.gnomeshift.recommendationservice.database.UserRatings
import com.gnomeshift.recommendationservice.dto.DetailedRecommendation
import com.gnomeshift.recommendationservice.dto.RatingEvent
import com.gnomeshift.recommendationservice.dto.RecommendationDTO
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.time.LocalDateTime

class RecommendationService {

    fun getRecommendations(userId: Long): List<RecommendationDTO> {
        return transaction {
            Recommendations
                .selectAll()
                .where { Recommendations.userId eq userId }
                .orderBy(Recommendations.score to SortOrder.DESC)
                .limit(10)
                .map {
                    RecommendationDTO(
                        courseId = it[Recommendations.courseId],
                        score = it[Recommendations.score],
                        reason = it[Recommendations.reason]
                    )
                }
        }
    }

    fun updateRecommendations(event: RatingEvent) {
        transaction {
            UserRatings.insert {
                it[userId] = event.userId
                it[courseId] = event.courseId
                it[rating] = event.rating
                it[createdAt] = LocalDateTime.now()
            }

            if (event.rating >= 4) {
                generateRecommendations(event.userId, event.courseId, event.rating)
            }
        }
    }

    private fun generateRecommendations(userId: Long, courseId: Long, rating: Int) {
        transaction {
            val categories = CourseCategories
                .selectAll()
                .where { CourseCategories.courseId eq courseId }
                .map { it[CourseCategories.category] }

            if (categories.isNotEmpty()) {
                val similarCourses = CourseCategories
                    .selectAll()
                    .where {
                        (CourseCategories.category inList categories) and
                                (CourseCategories.courseId neq courseId)
                    }
                    .map { it[CourseCategories.courseId] }
                    .distinct()

                similarCourses.forEach { similarCourseId ->
                    val existingRecommendation = Recommendations
                        .selectAll()
                        .where {
                            (Recommendations.userId eq userId) and
                                    (Recommendations.courseId eq similarCourseId)
                        }
                        .firstOrNull()

                    if (existingRecommendation == null) {
                        Recommendations.insert {
                            it[Recommendations.userId] = userId
                            it[Recommendations.courseId] = similarCourseId
                            it[score] = calculateScore(rating)
                            it[reason] = "Based on your interest in similar courses"
                            it[createdAt] = LocalDateTime.now()
                        }
                    } else {
                        Recommendations.update({
                            (Recommendations.userId eq userId) and
                                    (Recommendations.courseId eq similarCourseId)
                        }) {
                            it[score] = calculateScore(rating)
                            it[createdAt] = LocalDateTime.now()
                        }
                    }
                }
            }

            generateCollaborativeRecommendations(userId, courseId)
        }
    }

    private fun generateCollaborativeRecommendations(userId: Long, courseId: Long) {
        transaction {
            val similarUsers = UserRatings
                .selectAll()
                .where {
                    (UserRatings.courseId eq courseId) and
                            (UserRatings.rating greaterEq 4) and
                            (UserRatings.userId neq userId)
                }
                .map { it[UserRatings.userId] }
                .distinct()

            if (similarUsers.isNotEmpty()) {
                val recommendedCourses = UserRatings
                    .selectAll()
                    .where {
                        (UserRatings.userId inList similarUsers) and
                                (UserRatings.rating greaterEq 4) and
                                (UserRatings.courseId neq courseId)
                    }
                    .groupBy(UserRatings.courseId)
                    .map {
                        it[UserRatings.courseId] to
                                it[UserRatings.rating.avg()]?.toDouble()
                    }

                recommendedCourses.forEach { (recCourseId, avgRating) ->
                    val userAlreadyRated = UserRatings
                        .selectAll()
                        .where {
                            (UserRatings.userId eq userId) and
                                    (UserRatings.courseId eq recCourseId)
                        }
                        .firstOrNull() != null

                    if (!userAlreadyRated) {
                        val existingRec = Recommendations
                            .selectAll()
                            .where {
                                (Recommendations.userId eq userId) and
                                        (Recommendations.courseId eq recCourseId)
                            }
                            .firstOrNull()

                        val score = (avgRating ?: 0.0) * 0.7

                        if (existingRec == null) {
                            Recommendations.insert {
                                it[Recommendations.userId] = userId
                                it[Recommendations.courseId] = recCourseId
                                it[Recommendations.score] = score
                                it[reason] = "Users with similar interests also liked this"
                                it[createdAt] = LocalDateTime.now()
                            }
                        } else {
                            Recommendations.update({
                                (Recommendations.userId eq userId) and
                                        (Recommendations.courseId eq recCourseId)
                            }) {
                                it[Recommendations.score] = score
                                it[createdAt] = LocalDateTime.now()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun calculateScore(rating: Int): Double {
        return when(rating) {
            5 -> 1.0
            4 -> 0.8
            3 -> 0.5
            2 -> 0.3
            else -> 0.1
        }
    }

    fun getTopRecommendations(userId: Long, limit: Int = 10): List<DetailedRecommendation> {
        return transaction {
            Recommendations
                .selectAll()
                .where { Recommendations.userId eq userId }
                .orderBy(Recommendations.score to SortOrder.DESC)
                .limit(limit)
                .map {
                    DetailedRecommendation(
                        courseId = it[Recommendations.courseId],
                        score = it[Recommendations.score],
                        reason = it[Recommendations.reason],
                        createdAt = it[Recommendations.createdAt]
                    )
                }
        }
    }
}
