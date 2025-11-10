package com.gnomeshift.recommendationservice.kafka

import com.gnomeshift.recommendationservice.service.RecommendationService
import com.gnomeshift.recommendationservice.dto.RatingEvent
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.Properties

class KafkaConsumerService(private val recommendationService: RecommendationService) {
    private val logger = LoggerFactory.getLogger(KafkaConsumerService::class.java)
    private val consumer: KafkaConsumer<String, String>
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    init {
        val props = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                System.getenv("KAFKA_BOOTSTRAP_SERVERS") ?: "localhost:9092")
            put(ConsumerConfig.GROUP_ID_CONFIG, "recommendation-service-group")
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
            put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
            put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
        }

        consumer = KafkaConsumer(props)
        consumer.subscribe(listOf("ratings"))
        logger.info("Kafka consumer initialized and subscribed to 'ratings' topic")
    }

    suspend fun startConsuming() {
        logger.info("Starting Kafka consumer...")
        while (true) {
            try {
                val records = consumer.poll(Duration.ofMillis(100))
                for (record in records) {
                    try {
                        processRatingEvent(record.value())
                    }
                    catch (e: Exception) {
                        logger.error("Error processing individual record: ${e.message}", e)
                    }
                }
                delay(100)
            }
            catch (e: Exception) {
                logger.error("Error in consumer loop: ${e.message}", e)
                delay(5000)
            }
        }
    }

    private fun processRatingEvent(message: String) {
        try {
            val event = json.decodeFromString<RatingEvent>(message)
            recommendationService.updateRecommendations(event)
            logger.info("Successfully processed rating event for user ${event.userId} and course ${event.courseId}")
        }
        catch (e: Exception) {
            logger.error("Error processing rating event: ${e.message}", e)
        }
    }

    fun close() {
        consumer.close()
        logger.info("Kafka consumer closed")
    }
}
