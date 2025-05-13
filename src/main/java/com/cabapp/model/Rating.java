package com.cabapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "rating")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Min(1)
    @Max(5)
    private int rating;

    private String comment;

    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
