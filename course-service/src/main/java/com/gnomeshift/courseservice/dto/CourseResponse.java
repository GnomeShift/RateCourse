package com.gnomeshift.courseservice.dto;

import com.gnomeshift.courseservice.entity.CourseLevel;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private Integer duration;
    private CourseLevel level;
    private Double price;
    private LocalDateTime createdAt;
}
