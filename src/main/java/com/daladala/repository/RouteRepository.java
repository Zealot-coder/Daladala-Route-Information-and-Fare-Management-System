package com.daladala.repository;

import com.daladala.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {

    // All active routes — used on the passenger routes listing page
    List<Route> findByIsActiveTrue();

    // Keyword search: checks start point, end point, and via stops
    // LOWER + LIKE = case-insensitive partial match
    @Query("SELECT r FROM Route r WHERE r.isActive = true AND (" +
           "LOWER(r.startPoint) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.endPoint)   LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.viaStops)   LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Route> searchRoutes(@Param("keyword") String keyword);

    // From + To search — used when passenger fills both origin and destination fields
    @Query("SELECT r FROM Route r WHERE r.isActive = true AND " +
           "LOWER(r.startPoint) LIKE LOWER(CONCAT('%', :from, '%')) AND " +
           "LOWER(r.endPoint)   LIKE LOWER(CONCAT('%', :to, '%'))")
    List<Route> findByFromAndTo(@Param("from") String from, @Param("to") String to);

    // Check for duplicate route number before admin saves a new route
    Optional<Route> findByRouteNumber(String routeNumber);
}
