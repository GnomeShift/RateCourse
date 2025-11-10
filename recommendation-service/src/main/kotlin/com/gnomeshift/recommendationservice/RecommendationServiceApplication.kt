package com.gnomeshift.recommendationservice

import com.gnomeshift.recommendationservice.database.DatabaseFactory
import com.gnomeshift.recommendationservice.kafka.KafkaConsumerService
import com.gnomeshift.recommendationservice.service.RecommendationService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    DatabaseFactory.init(environment)

    val recommendationService = RecommendationService()
    val kafkaConsumer = KafkaConsumerService(recommendationService)

    GlobalScope.launch {
        kafkaConsumer.startConsuming()
    }

    routing {
        route("/api/recommendations") {
            get("/{userId}") {
                val userId = call.parameters["userId"]?.toLongOrNull()
                if (userId != null) {
                    val recommendations = recommendationService.getRecommendations(userId)
                    call.respond(HttpStatusCode.OK, recommendations)
                }
                else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid user ID"))
                }
            }

            get("/{userId}/top") {
                val userId = call.parameters["userId"]?.toLongOrNull()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10

                if (userId != null) {
                    val recommendations = recommendationService.getTopRecommendations(userId, limit)
                    call.respond(HttpStatusCode.OK, recommendations)
                }
                else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid user ID"))
                }
            }
        }

        get("/health") {
            call.respond(HttpStatusCode.OK, mapOf("status" to "healthy"))
        }
    }
}
