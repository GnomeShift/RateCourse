package com.gnomeshift.courseservice.dto;

import com.gnomeshift.courseservice.entity.CourseLevel;
import lombok.Data;

@Data
public class CourseRequest {
    private String title;
    private String description;
    private String category;
    private Integer duration;
    private CourseLevel level;
    private Double price;
}
