package com.daladala.repository;

import com.daladala.entity.RouteHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteHistoryRepository extends JpaRepository<RouteHistory, Long> {

    // All changes for one route — shown on the route detail page
    // OrderByChangedAtDesc = newest change first
    List<RouteHistory> findByRouteIdOrderByChangedAtDesc(Long routeId);

    // All history across all routes — shown on the admin dashboard
    List<RouteHistory> findAllByOrderByChangedAtDesc();
}
