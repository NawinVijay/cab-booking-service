package com.cabapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class TripRequestDTO {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "pickUpLocation is required")
    private String pickUpLocation;

    @NotNull(message = "dropLocation is required")
    private String dropLocation;

    @NotNull(message = "scheduledTime is required")
    @Future(message = "scheduledTime must be future date time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime scheduledTime;

}
