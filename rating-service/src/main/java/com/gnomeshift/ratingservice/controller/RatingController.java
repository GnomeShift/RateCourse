package com.gnomeshift.ratingservice.controller;

import com.gnomeshift.ratingservice.dto.CourseRatingStats;
import com.gnomeshift.ratingservice.dto.RatingRequest;
import com.gnomeshift.ratingservice.dto.RatingResponse;
import com.gnomeshift.ratingservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingResponse> addRating(@RequestBody RatingRequest request, @RequestHeader("X-User-Id") Long userId) {
        request.setUserId(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.addRating(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RatingResponse> updateRating(@PathVariable Long id, @RequestBody RatingRequest request) {
        return ResponseEntity.ok(ratingService.updateRating(id, request));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<RatingResponse>> getCourseRatings(@PathVariable Long courseId) {
        return ResponseEntity.ok(ratingService.getCourseRatings(courseId));
    }

    @GetMapping("/course/{courseId}/stats")
    public ResponseEntity<CourseRatingStats> getCourseStats(@PathVariable Long courseId) {
        return ResponseEntity.ok(ratingService.getCourseStats(courseId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RatingResponse>> getUserRatings(@PathVariable Long userId) {
        return ResponseEntity.ok(ratingService.getUserRatings(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
}
