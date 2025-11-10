package com.gnomeshift.ratingservice.repository;

import com.gnomeshift.ratingservice.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByCourseId(Long courseId);
    List<Rating> findByUserId(Long userId);
    Optional<Rating> findByUserIdAndCourseId(Long userId, Long courseId);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.courseId = :courseId")
    Double getAverageRatingByCourseId(Long courseId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.courseId = :courseId")
    Long countByCourseId(Long courseId);
}
