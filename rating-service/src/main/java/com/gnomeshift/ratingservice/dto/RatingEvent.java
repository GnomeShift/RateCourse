package com.gnomeshift.ratingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingEvent {
    private Long userId;
    private Long courseId;
    private Integer rating;
    private String eventType = "RATING_CREATED";
    private Long timestamp;
}
