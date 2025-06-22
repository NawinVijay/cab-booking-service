package com.cabapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "driver_availability")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Column(name = "available_from")
    private OffsetDateTime availableFrom;

    @Column(name = "available_to")
    private OffsetDateTime availableTo;
}
