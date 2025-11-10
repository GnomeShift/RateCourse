package com.gnomeshift.ratingservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RatingResponse {
    private Long id;
    private Long userId;
    private Long courseId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
