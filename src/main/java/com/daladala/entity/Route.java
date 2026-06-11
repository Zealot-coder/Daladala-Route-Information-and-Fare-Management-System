package com.daladala.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data                           // Lombok: generates getters, setters, toString, equals, hashCode
@Entity                         // Marks this class as a JPA entity (mapped to a DB table)
@Table(name = "routes")         // Maps to the "routes" table in MySQL
public class Route {

    @Id                                                      // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)      // AUTO_INCREMENT in MySQL
    private Long id;

    @NotBlank(message = "Route number is required")
    @Column(name = "route_number", nullable = false, unique = true, length = 20)
    private String routeNumber;

    @NotBlank(message = "Start point is required")
    @Column(name = "start_point", nullable = false, length = 100)
    private String startPoint;

    @NotBlank(message = "End point is required")
    @Column(name = "end_point", nullable = false, length = 100)
    private String endPoint;

    @Column(name = "via_stops", length = 300)  // Optional — nullable by default
    private String viaStops;

    @NotNull(message = "Distance is required")
    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    @NotNull(message = "Fare is required")
    @Min(value = 1, message = "Fare must be greater than 0")
    @Column(name = "fare_tzs", nullable = false)
    private Integer fareTzs;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)  // updatable = false: never changed after insert
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist   // Runs automatically before INSERT
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
    }

    @PreUpdate    // Runs automatically before UPDATE
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
