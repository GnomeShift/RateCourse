package com.gnomeshift.ratingservice.dto;

import lombok.Data;

@Data
public class RatingRequest {
    private Long userId;
    private Long courseId;
    private Integer rating;
    private String comment;
}
