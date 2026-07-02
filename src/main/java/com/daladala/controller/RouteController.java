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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;
    private final RouteHistoryService routeHistoryService;

    // HOME
    @GetMapping("/")
    public String index(Model model) {
        List<Route> routes = routeService.getAllActiveRoutes();
        int minFare  = routes.stream().mapToInt(Route::getFareTzs).min().orElse(0);
        long areas   = routes.stream().map(Route::getStartPoint).distinct().count();

        model.addAttribute("routes",      routes);
        model.addAttribute("totalRoutes", routes.size());
        model.addAttribute("minFare",     minFare);
        model.addAttribute("areas",       areas);
        model.addAttribute("activePage",  "home");
        return "index";
    }

    // ALL ROUTES
    @GetMapping("/routes")
    public String listRoutes(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer minFare,
            @RequestParam(required = false) Integer maxFare,
            Model model) {

        List<Route> routes = filterAndSort(routeService.getAllActiveRoutes(), sort, minFare, maxFare);

        model.addAttribute("routes",     routes);
        model.addAttribute("sort",       sort);
        model.addAttribute("minFare",    minFare);
        model.addAttribute("maxFare",    maxFare);
        model.addAttribute("activePage", "routes");
        return "routes";
    }

    // SEARCH
    @GetMapping("/routes/search")
    public String searchRoutes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer minFare,
            @RequestParam(required = false) Integer maxFare,
            Model model) {

        List<Route> results;
        if (hasValue(from) && hasValue(to)) {
            results = routeService.searchByFromAndTo(from, to);
        } else if (hasValue(keyword)) {
            results = routeService.searchRoutes(keyword);
        } else {
            results = routeService.getAllActiveRoutes();
        }

        results = filterAndSort(results, sort, minFare, maxFare);

        model.addAttribute("routes",     results);
        model.addAttribute("keyword",    keyword);
        model.addAttribute("from",       from);
        model.addAttribute("to",         to);
        model.addAttribute("sort",       sort);
        model.addAttribute("minFare",    minFare);
        model.addAttribute("maxFare",    maxFare);
        model.addAttribute("activePage", "search");
        return "routes";
    }

    // ROUTE DETAIL
    @GetMapping("/routes/{id}")
    public String viewRoute(@PathVariable Long id, Model model) {
        Route route = routeService.getRouteById(id);
        List<RouteHistory> history = routeHistoryService.getHistoryByRoute(id);
        model.addAttribute("route",      route);
        model.addAttribute("history",    history);
        model.addAttribute("activePage", "routes");
        return "route-detail";
    }

    // FARE CALCULATOR
    @GetMapping("/calculate")
    public String fareCalculator(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            Model model) {

        model.addAttribute("from",       from);
        model.addAttribute("to",         to);
        model.addAttribute("activePage", "calculate");

        if (hasValue(from) && hasValue(to)) {
            List<Route> results = routeService.searchByFromAndTo(from, to);
            model.addAttribute("results",  results);
            model.addAttribute("searched", true);
        }

        return "fare-calculator";
    }

    // HELPERS
    private List<Route> filterAndSort(List<Route> routes, String sort, Integer minFare, Integer maxFare) {
        List<Route> list = new ArrayList<>(routes);

        if (minFare != null) list = list.stream().filter(r -> r.getFareTzs() >= minFare).collect(Collectors.toList());
        if (maxFare != null) list = list.stream().filter(r -> r.getFareTzs() <= maxFare).collect(Collectors.toList());

        if      ("fare_asc".equals(sort))      list.sort(Comparator.comparingInt(Route::getFareTzs));
        else if ("fare_desc".equals(sort))     list.sort(Comparator.comparingInt(Route::getFareTzs).reversed());
        else if ("distance_asc".equals(sort))  list.sort(Comparator.comparingDouble(Route::getDistanceKm));
        else if ("distance_desc".equals(sort)) list.sort(Comparator.comparingDouble(Route::getDistanceKm).reversed());
        else if ("route_asc".equals(sort))     list.sort(Comparator.comparing(Route::getRouteNumber));

        return list;
    }

    private boolean hasValue(String s) {
        return s != null && !s.isBlank();
    }
}
