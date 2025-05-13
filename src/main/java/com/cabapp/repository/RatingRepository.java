package com.cabapp.repository;

import com.cabapp.model.Rating;
import com.cabapp.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Rating findByTrip(Trip trip);
}