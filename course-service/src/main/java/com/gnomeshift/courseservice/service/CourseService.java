package com.gnomeshift.courseservice.service;

import com.gnomeshift.courseservice.dto.CourseRequest;
import com.gnomeshift.courseservice.dto.CourseResponse;
import com.gnomeshift.courseservice.entity.Course;
import com.gnomeshift.courseservice.exception.CourseNotFoundException;
import com.gnomeshift.courseservice.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {
    private final CourseRepository courseRepository;

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public Optional<CourseResponse> getCourseById(Long id) {
        return courseRepository.findById(id).map(this::convertToResponse);
    }

    public CourseResponse createCourse(CourseRequest request) {
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCategory(request.getCategory());
        course.setDuration(request.getDuration());
        course.setLevel(request.getLevel());
        course.setPrice(request.getPrice());

        Course savedCourse = courseRepository.save(course);
        return convertToResponse(savedCourse);
    }

    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + id));

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCategory(request.getCategory());
        course.setDuration(request.getDuration());
        course.setLevel(request.getLevel());
        course.setPrice(request.getPrice());

        Course updatedCourse = courseRepository.save(course);
        return convertToResponse(updatedCourse);
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new CourseNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    public List<CourseResponse> searchCourses(String query) {
        return courseRepository.findByTitleContainingIgnoreCase(query).stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<CourseResponse> getCoursesByCategory(String category) {
        return courseRepository.findByCategory(category).stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    private CourseResponse convertToResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setTitle(course.getTitle());
        response.setDescription(course.getDescription());
        response.setCategory(course.getCategory());
        response.setDuration(course.getDuration());
        response.setLevel(course.getLevel());
        response.setPrice(course.getPrice());
        response.setCreatedAt(course.getCreatedAt());
        return response;
    }
}
