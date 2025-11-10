package com.gnomeshift.ratingservice.kafka;

import com.gnomeshift.ratingservice.dto.RatingEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendRatingEvent(RatingEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("ratings", message);
            log.info("Sent rating event: {}", message);
        }
        catch (Exception e) {
            log.error("Error sending rating event", e);
        }
    }
}
