package com.gnomeshift.recommendationservice.database

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.datetime
import java.time.LocalDateTime

object Recommendations : Table("recommendations") {
    val id = long("id").autoIncrement()
    val userId = long("user_id")
    val courseId = long("course_id")
    val score = double("score")
    val reason = varchar("reason", 255).nullable()
    val createdAt = datetime("created_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}

object UserRatings : Table("user_ratings") {
    val id = long("id").autoIncrement()
    val userId = long("user_id")
    val courseId = long("course_id")
    val rating = integer("rating")
    val createdAt = datetime("created_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}

object CourseCategories : Table("course_categories") {
    val courseId = long("course_id")
    val category = varchar("category", 100)

    override val primaryKey = PrimaryKey(courseId, category)
}
