package com.cabapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "location_update")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationUpdate {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    @JsonIgnore
    private Trip trip;

    private Double latitude;
    private Double longitude;
    private OffsetDateTime timestamp = OffsetDateTime.now();
}