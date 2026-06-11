package com.daladala.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "route_history")
public class RouteHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Stores the route's ID as a plain number — no @ManyToOne relationship.
    // This keeps history intact even if the route is later deleted.
    @Column(name = "route_id", nullable = false)
    private Long routeId;

    // Value must be one of: CREATED, UPDATED, DELETED
    @Column(name = "action", nullable = false, length = 20)
    private String action;

    @Column(name = "change_description", nullable = false, length = 500)
    private String changeDescription;

    // Admin username stored as text — not FK — so history survives admin deletion
    @Column(name = "changed_by", nullable = false, length = 50)
    private String changedBy;

    @Column(name = "changed_at", updatable = false)
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }
}
