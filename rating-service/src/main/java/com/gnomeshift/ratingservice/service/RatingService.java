package com.gnomeshift.ratingservice.service;

import com.gnomeshift.ratingservice.dto.CourseRatingStats;
import com.gnomeshift.ratingservice.dto.RatingEvent;
import com.gnomeshift.ratingservice.dto.RatingRequest;
import com.gnomeshift.ratingservice.dto.RatingResponse;
import com.gnomeshift.ratingservice.entity.Rating;
import com.gnomeshift.ratingservice.exception.RatingAlreadyExistsException;
import com.gnomeshift.ratingservice.exception.InvalidRatingException;
import com.gnomeshift.ratingservice.kafka.RatingEventProducer;
import com.gnomeshift.ratingservice.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RatingService {
    private final RatingRepository ratingRepository;
    private final RatingEventProducer eventProducer;

    public RatingResponse addRating(RatingRequest request) {
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new InvalidRatingException("Rating must be between 1 and 5");
        }

        ratingRepository.findByUserIdAndCourseId(request.getUserId(), request.getCourseId())
                .ifPresent(r -> {
                    throw new RatingAlreadyExistsException("User already rated this course");
                });

        Rating rating = new Rating();
        rating.setUserId(request.getUserId());
        rating.setCourseId(request.getCourseId());
        rating.setRating(request.getRating());
        rating.setComment(request.getComment());
        Rating savedRating = ratingRepository.save(rating);

        RatingEvent event = new RatingEvent(savedRating.getUserId(), savedRating.getCourseId(), savedRating.getRating(), "RATING_CREATED", System.currentTimeMillis());
        eventProducer.sendRatingEvent(event);

        log.info("Rating created for course {} by user {}", savedRating.getCourseId(), savedRating.getUserId());
        return convertToResponse(savedRating);
    }

    public RatingResponse updateRating(Long ratingId, RatingRequest request) {
        Rating rating = ratingRepository.findById(ratingId).orElseThrow(() -> new RuntimeException("Rating not found"));

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new InvalidRatingException("Rating must be between 1 and 5");
        }

        rating.setRating(request.getRating());
        rating.setComment(request.getComment());
        Rating updatedRating = ratingRepository.save(rating);

        RatingEvent event = new RatingEvent(updatedRating.getUserId(), updatedRating.getCourseId(), updatedRating.getRating(), "RATING_UPDATED", System.currentTimeMillis());
        eventProducer.sendRatingEvent(event);
        return convertToResponse(updatedRating);
    }

    public List<RatingResponse> getCourseRatings(Long courseId) {
        return ratingRepository.findByCourseId(courseId).stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<RatingResponse> getUserRatings(Long userId) {
        return ratingRepository.findByUserId(userId).stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public CourseRatingStats getCourseStats(Long courseId) {
        Double avgRating = ratingRepository.getAverageRatingByCourseId(courseId);
        Long totalRatings = ratingRepository.countByCourseId(courseId);
        return new CourseRatingStats(courseId, avgRating != null ? avgRating : 0.0, totalRatings);
    }

    public void deleteRating(Long ratingId) {
        Rating rating = ratingRepository.findById(ratingId).orElseThrow(() -> new RuntimeException("Rating not found"));
        ratingRepository.delete(rating);

        RatingEvent event = new RatingEvent(rating.getUserId(), rating.getCourseId(), rating.getRating(), "RATING_DELETED", System.currentTimeMillis());
        eventProducer.sendRatingEvent(event);
    }

    private RatingResponse convertToResponse(Rating rating) {
        RatingResponse response = new RatingResponse();
        response.setId(rating.getId());
        response.setUserId(rating.getUserId());
        response.setCourseId(rating.getCourseId());
        response.setRating(rating.getRating());
        response.setComment(rating.getComment());
        response.setCreatedAt(rating.getCreatedAt());
        return response;
    }
}
