package com.cabapp.dto;

import com.cabapp.model.Trip;
import com.cabapp.model.TripStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class TripResponseDTO {

    private Long tripId;
    private String pickUpLocation;
    private String dropLocation;
    private OffsetDateTime scheduledTime;
    private String driverName;
    private String userName;
    private TripStatus status;
    private double cost;

    public static TripResponseDTO fromEntity(Trip trip) {
        return TripResponseDTO.builder()
                .tripId(trip.getId())
                .pickUpLocation(trip.getPickUpLocation())
                .dropLocation(trip.getDropLocation())
                .scheduledTime(trip.getScheduledTime())
                .driverName(trip.getDriver().getName())
                .userName(trip.getUser().getName())
                .status(trip.getStatus())
                .cost(trip.getCost())
                .build();
    }
}
