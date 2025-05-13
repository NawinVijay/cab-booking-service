package com.cabapp.repository;

import com.cabapp.model.Driver;
import com.cabapp.model.Trip;
import com.cabapp.model.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Trip findByUserId(Long userId);
    List<Trip> findByDriverId(Long driverId);
    List<Trip> findByStatus(TripStatus status);
    List<Trip> findByScheduledTimeBetween(OffsetDateTime start, OffsetDateTime end);

    List<Trip> findByDriverAndRatingIsNotNull(Driver driver);

    @Query("SELECT t FROM Trip t WHERE t.driver.id = :driverId AND t.scheduledTime = :scheduledTime")
    Optional<Trip> findTripAtTimeForDriver(@Param("driverId") Long driverId, @Param("scheduledTime") OffsetDateTime scheduledTime);

    @Query("SELECT t FROM Trip t WHERE t.driver.id = :driverId AND t.status <> 'CANCELLED' AND " +
            "((:scheduledStartTime BETWEEN t.startTime AND t.endTime) " +
            "OR (:scheduledEndTime BETWEEN t.startTime AND t.endTime) " +
            "OR (t.startTime BETWEEN :scheduledStartTime AND :scheduledEndTime)" +
            "OR (t.scheduledTime BETWEEN :scheduledStartTime AND :scheduledEndTime))")
    List<Trip> findOverlappingTrips(@Param("driverId") Long driverId,
                                    @Param("scheduledStartTime") OffsetDateTime scheduledStartTime,
                                    @Param("scheduledEndTime") OffsetDateTime scheduledEndTime);


    // For user history
    List<Trip> findByUserIdOrderByScheduledTimeDesc(Long userId);

    // For driver history
    List<Trip> findByDriverIdOrderByScheduledTimeDesc(Long driverId);

    // Optional: find ongoing trips
    List<Trip> findByStatus(String status);

    // Optional: for driver’s current trip
    List<Trip> findByDriverIdAndStatus(Long driverId, String status);

}
