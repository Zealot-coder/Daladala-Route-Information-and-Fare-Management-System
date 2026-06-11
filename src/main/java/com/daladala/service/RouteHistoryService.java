package com.daladala.service;

import com.daladala.entity.RouteHistory;
import com.daladala.repository.RouteHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor  // Lombok: generates constructor for all final fields (no @Autowired needed)
public class RouteHistoryService {

    private final RouteHistoryRepository routeHistoryRepository;

    // Called by RouteService every time a route is created, updated, or deleted
    public void log(Long routeId, String action, String description, String changedBy) {
        RouteHistory history = new RouteHistory();
        history.setRouteId(routeId);
        history.setAction(action);
        history.setChangeDescription(description);
        history.setChangedBy(changedBy);
        routeHistoryRepository.save(history);
    }

    // History for one route — shown on route detail page
    public List<RouteHistory> getHistoryByRoute(Long routeId) {
        return routeHistoryRepository.findByRouteIdOrderByChangedAtDesc(routeId);
    }

    // All history — shown on admin dashboard
    public List<RouteHistory> getAllHistory() {
        return routeHistoryRepository.findAllByOrderByChangedAtDesc();
    }
}
