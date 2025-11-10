package com.gnomeshift.ratingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CourseRatingStats {
    private Long courseId;
    private Double averageRating;
    private Long totalRatings;
}
