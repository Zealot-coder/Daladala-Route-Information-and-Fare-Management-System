package com.daladala.service;

import com.daladala.entity.Route;
import com.daladala.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final RouteHistoryService routeHistoryService;

    // Passengers see only active routes
    public List<Route> getAllActiveRoutes() {
        return routeRepository.findByIsActiveTrue();
    }

    // Admin sees all routes including inactive ones
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    // Single route by ID — throws exception if not found
    public Route getRouteById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found with id: " + id));
    }

    // Keyword search: matches start point, end point, or via stops
    public List<Route> searchRoutes(String keyword) {
        return routeRepository.searchRoutes(keyword);
    }

    // Two-field search: passenger fills "From" and "To" separately
    public List<Route> searchByFromAndTo(String from, String to) {
        return routeRepository.findByFromAndTo(from, to);
    }

    // Used before saving to prevent duplicate route numbers
    public boolean routeNumberExists(String routeNumber) {
        return routeRepository.findByRouteNumber(routeNumber).isPresent();
    }

    // Add new route — logs CREATED history
    public void saveRoute(Route route, String adminUsername) {
        routeRepository.save(route);
        routeHistoryService.log(
                route.getId(),
                "CREATED",
                "Route " + route.getRouteNumber() + " added: " +
                route.getStartPoint() + " → " + route.getEndPoint() +
                ", fare " + route.getFareTzs() + " TZS",
                adminUsername
        );
    }

    // Update existing route — detects fare changes for meaningful history description
    public void updateRoute(Route updatedRoute, String adminUsername) {
        Route existing = getRouteById(updatedRoute.getId());
        String description = buildUpdateDescription(existing, updatedRoute);
        routeRepository.save(updatedRoute);
        routeHistoryService.log(updatedRoute.getId(), "UPDATED", description, adminUsername);
    }

    // Soft delete: sets is_active = false instead of removing the row.
    // Preserves route_history records (which have a FK to this route).
    public void deleteRoute(Long id, String adminUsername) {
        Route route = getRouteById(id);
        route.setIsActive(false);
        routeRepository.save(route);
        routeHistoryService.log(
                id,
                "DELETED",
                "Route " + route.getRouteNumber() + " deactivated (" +
                route.getStartPoint() + " → " + route.getEndPoint() + ")",
                adminUsername
        );
    }

    // Builds a human-readable description of what changed
    private String buildUpdateDescription(Route old, Route updated) {
        if (!old.getFareTzs().equals(updated.getFareTzs())) {
            return "Fare updated: " + old.getFareTzs() + " TZS → " +
                   updated.getFareTzs() + " TZS for route " + updated.getRouteNumber();
        }
        return "Route details updated for " + updated.getRouteNumber();
    }
}
