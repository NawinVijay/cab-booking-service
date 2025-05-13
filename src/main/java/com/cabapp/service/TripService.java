package com.cabapp.service;

import com.cabapp.dto.TripRequestDTO;
import com.cabapp.exception.CustomException;
import com.cabapp.model.*;
import com.cabapp.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final DriverRepository driverRepository;
    private final DriverAvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    private static final int ESTIMATED_TRIP_DURATION_MINUTES = 30;
    private static final int CANCELLATION_WINDOW_MINUTES = 60;

    /**
     * Books a trip and assigns a driver fairly.
     */
    @Transactional
    public Trip bookTrip(TripRequestDTO request) throws CustomException {
        validateBookingRequest(request.getUserId(), request.getPickUpLocation(), request.getDropLocation(), request.getScheduledTime());

        // 1. Basic validation
        if (request.getPickUpLocation().equalsIgnoreCase(request.getDropLocation()))
            throw new CustomException("Pickup and Drop locations cannot be the same");

        // 2. Get user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException("User not found"));

        // 3. Fetch all available drivers at scheduled time
        List<DriverAvailability> availabilities = availabilityRepository.findDriverAvailabilityForTheScheduledTime(request.getScheduledTime());

        if (availabilities.isEmpty())
            throw new CustomException("No driver available for the scheduled time");

        // 4. Filter online drivers only
        List<Driver> eligibleDrivers = availabilities.stream()
                .map(DriverAvailability::getDriver)
                .filter(driver -> Boolean.TRUE.equals(driver.isOnline()))
                .distinct()
                .toList();

        // 5. Remove already booked drivers
        OffsetDateTime estimatedEndTime = request.getScheduledTime().plusMinutes(ESTIMATED_TRIP_DURATION_MINUTES);
        List<Driver> freeDrivers = new ArrayList<>();
        for (Driver driver : eligibleDrivers) {
            if (tripRepository.findOverlappingTrips(driver.getId(), request.getScheduledTime(), estimatedEndTime).isEmpty())
                freeDrivers.add(driver);
        }
        if (freeDrivers.isEmpty())
            throw new CustomException("No available drivers found for the scheduled time.");

        // 6. Fairness: Sort drivers by rating desc
        Driver selectedDriver = freeDrivers.stream()
                .sorted(Comparator.comparingDouble(Driver::getRating).reversed())
                .findFirst()
                .orElseThrow(() -> new CustomException("No suitable driver found"));

        // Step 4: Create and save trip
        Trip trip = Trip.builder()
                .user(user)
                .driver(selectedDriver)
                .pickUpLocation(request.getPickUpLocation())
                .dropLocation(request.getDropLocation())
                .scheduledTime(request.getScheduledTime())
                .status(TripStatus.BOOKED)
                .cost(10.0) // base cost
                .build();

        return tripRepository.save(trip);
    }

    private void validateBookingRequest(Long userId, String pickup, String drop, OffsetDateTime scheduledTime) {
        if (userId == null)
            throw new IllegalArgumentException("UserId is required");

        if (pickup == null || pickup.isBlank())
            throw new IllegalArgumentException("Pickup location is required");

        if (drop == null || drop.isBlank())
            throw new IllegalArgumentException("Drop location is required");

        if (pickup.equalsIgnoreCase(drop))
            throw new IllegalArgumentException("Pickup and drop locations cannot be the same");

        if (scheduledTime == null || scheduledTime.isBefore(OffsetDateTime.now()))
            throw new CustomException("Scheduled time must be in the future");
    }



    @Transactional
    public Trip cancelTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new CustomException("Trip not found"));

        if (trip.getStatus() == TripStatus.CANCELLED)
            throw new RuntimeException("Trip is already cancelled");

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        Duration timeUntilTrip = Duration.between(now, trip.getScheduledTime());

        if (timeUntilTrip.toMinutes() < CANCELLATION_WINDOW_MINUTES)
            throw new RuntimeException("Cannot cancel trip within 1 hour of scheduled time");

        trip.setStatus(TripStatus.CANCELLED);
        return tripRepository.save(trip);
    }

    public Trip rateTrip(Long tripId, int rating) {
        if (rating < 1 || rating > 5)
            throw new RuntimeException("Rating must be between 1 and 5");

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (trip.getRating() != null)
            throw new RuntimeException("Trip already rated");

        if (trip.getStatus() != TripStatus.COMPLETED)
            throw new RuntimeException("Only completed trips can be rated");

        trip.setRating(rating);
        tripRepository.save(trip);

        // update driver's average rating
        Driver driver = trip.getDriver();
        List<Trip> ratedTrips = tripRepository.findByDriverAndRatingIsNotNull(driver);
        double avgRating = ratedTrips.stream()
                .mapToInt(Trip::getRating)
                .average()
                .orElse(0.0);

        driver.setRating(avgRating);
        driverRepository.save(driver);

        return trip;
    }

    public List<Trip> getUserHistory(Long userId) {
        return tripRepository.findByUserIdOrderByScheduledTimeDesc(userId);
    }

    public List<Trip> getDriverHistory(Long driverId) {
        return tripRepository.findByDriverIdOrderByScheduledTimeDesc(driverId);
    }

    public Trip completeTrip(Long tripId, double distance) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getStatus().equals(TripStatus.IN_PROGRESS)) {
            throw new RuntimeException("Trip must be in progress to complete");
        }

        double baseFare = 50.0;
        double costPerKm = 10.0;
        double totalCost = baseFare + (costPerKm * distance);

        trip.setDistance(distance);
        trip.setCost(totalCost);
        trip.setStatus(TripStatus.COMPLETED);
        trip.setPaymentStatus(PaymentStatus.PENDING);

        return tripRepository.save(trip);
    }

    public Trip processPayment(Long tripId, String method) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getStatus().equals(TripStatus.COMPLETED)) {
            throw new RuntimeException("Only completed trips can be paid");
        }

        trip.setPaymentStatus(PaymentStatus.COMPLETED); // Simulate payment
        return tripRepository.save(trip);
    }
}
