package com.cabapp.service;

import com.cabapp.model.LocationUpdate;
import com.cabapp.model.Trip;
import com.cabapp.repository.LocationRepository;
import com.cabapp.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final TripRepository tripRepository;
    private final LocationRepository locationRepo;

    public LocationUpdate updateLocation(Long tripId, double lat, double lng) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        LocationUpdate loc = new LocationUpdate();
        loc.setTrip(trip);
        loc.setLatitude(lat);
        loc.setLongitude(lng);
        return locationRepo.save(loc);
    }
}
