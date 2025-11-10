package com.gnomeshift.courseservice.repository;

import com.gnomeshift.courseservice.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByCategory(String category);
    List<Course> findByTitleContainingIgnoreCase(String title);
}
