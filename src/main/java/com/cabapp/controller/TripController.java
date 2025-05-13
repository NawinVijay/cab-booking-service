package com.cabapp.controller;

import com.cabapp.dto.TripRequestDTO;
import com.cabapp.dto.TripResponseDTO;
import com.cabapp.model.LocationUpdate;
import com.cabapp.model.Trip;
import com.cabapp.service.LocationService;
import com.cabapp.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cab/trips")
@RequiredArgsConstructor
@Validated
public class TripController {

    @Autowired
    private final TripService tripService;

    @Autowired
    private final LocationService locationService;

    @PostMapping("/book")
    public ResponseEntity<?> bookTrip(@Valid @RequestBody TripRequestDTO request) {

        try {
        Trip trip = tripService.bookTrip(request);
        TripResponseDTO responseDTO = TripResponseDTO.fromEntity(trip);
        // Build URI for Location header
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDTO.getTripId())
                .toUri();

        return ResponseEntity.created(location).body(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{tripId}/cancel")
    public ResponseEntity<?> cancelTrip(@PathVariable Long tripId) {
        try {
            Trip cancelledTrip = tripService.cancelTrip(tripId);
            return ResponseEntity.ok(cancelledTrip);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{tripId}/rate")
    public ResponseEntity<?> rateTrip(@PathVariable Long tripId, @RequestBody Map<String, Integer> payload) {
        try {
            int rating = payload.get("rating");
            Trip ratedTrip = tripService.rateTrip(tripId, rating);
            return ResponseEntity.ok(ratedTrip);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<Trip>> getUserTripHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(tripService.getUserHistory(userId));
    }

    @GetMapping("/driver/{driverId}/history")
    public ResponseEntity<List<Trip>> getDriverTripHistory(@PathVariable Long driverId) {
        return ResponseEntity.ok(tripService.getDriverHistory(driverId));
    }

    @PutMapping("/{tripId}/complete")
    public Trip completeTrip(@PathVariable Long tripId, @RequestParam double distance) {
        return tripService.completeTrip(tripId, distance);
    }

    @PostMapping("/{tripId}/pay")
    public Trip pay(@PathVariable Long tripId, @RequestParam String method) {
        return tripService.processPayment(tripId, method);
    }

    @PostMapping("/{tripId}/location")
    public LocationUpdate updateLocation(@PathVariable Long tripId,
                                         @RequestParam double lat,
                                         @RequestParam double lng) {
        return locationService.updateLocation(tripId, lat, lng);
    }

    @GetMapping("/error")
    public String throwError() {
        throw new RuntimeException("Something went wrong!");
    }
}
