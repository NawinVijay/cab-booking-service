package com.cabapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "trip")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pickup_location")
    private String pickUpLocation;

    @Column(name = "drop_location")
    private String dropLocation;

    @Column(name = "scheduled_time")
    private OffsetDateTime scheduledTime;

    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    @Enumerated(EnumType.STRING)
    private TripStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore  // 🔥 Prevent infinite recursion when serializing
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    @JsonIgnore  // 🔥 Prevent infinite recursion when serializing
    private Driver driver;

    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column
    private Integer rating;  // Nullable

    @Column
    private Double distance; // in km

    @Column
    private Double cost = 10.0;     // Total cost

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus; // PENDING, COMPLETED, FAILED

}