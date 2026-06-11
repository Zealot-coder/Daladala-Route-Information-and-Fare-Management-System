package com.daladala.controller;

import com.daladala.entity.Route;
import com.daladala.entity.RouteHistory;
import com.daladala.service.RouteHistoryService;
import com.daladala.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;
    private final RouteHistoryService routeHistoryService;

    // Home page — shows all active routes
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("routes", routeService.getAllActiveRoutes());
        return "index";
    }

    // Full route listing page
    @GetMapping("/routes")
    public String listRoutes(Model model) {
        model.addAttribute("routes", routeService.getAllActiveRoutes());
        return "routes";
    }

    // Search page — handles both single-keyword and from/to searches
    // All three params are optional — shows all routes if nothing is entered
    @GetMapping("/routes/search")
    public String searchRoutes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            Model model) {

        List<Route> results;

        if (hasValue(from) && hasValue(to)) {
            results = routeService.searchByFromAndTo(from, to);
        } else if (hasValue(keyword)) {
            results = routeService.searchRoutes(keyword);
        } else {
            results = routeService.getAllActiveRoutes();
        }

        model.addAttribute("routes", results);
        model.addAttribute("keyword", keyword);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "routes";
    }

    // Route detail page — shows fare info + change history
    @GetMapping("/routes/{id}")
    public String viewRoute(@PathVariable Long id, Model model) {
        Route route = routeService.getRouteById(id);
        List<RouteHistory> history = routeHistoryService.getHistoryByRoute(id);
        model.addAttribute("route", route);
        model.addAttribute("history", history);
        return "route-detail";
    }

    private boolean hasValue(String s) {
        return s != null && !s.isBlank();
    }
}
