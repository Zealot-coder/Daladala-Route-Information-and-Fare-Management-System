package com.daladala.controller;

import com.daladala.entity.Admin;
import com.daladala.entity.Route;
import com.daladala.repository.AdminRepository;
import com.daladala.service.RouteHistoryService;
import com.daladala.service.RouteService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RouteService routeService;
    private final RouteHistoryService routeHistoryService;
    private final AdminRepository adminRepository;

    // ── LOGIN ────────────────────────────────────────────────────────────────

    @GetMapping("/login")
    public String loginPage() {
        return "admin-login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        Optional<Admin> found = adminRepository.findByUsername(username);

        if (found.isPresent() && found.get().getPassword().equals(password)) {
            session.setAttribute("adminUsername", username);
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("error", "Invalid username or password");
        return "admin-login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    // ── DASHBOARD ────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/admin/login";

        model.addAttribute("routes",        routeService.getAllRoutes());
        model.addAttribute("recentHistory", routeHistoryService.getAllHistory());
        return "admin-dashboard";
    }

    // ── ADD ROUTE ─────────────────────────────────────────────────────────────

    @GetMapping("/routes/new")
    public String showAddForm(HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/admin/login";
        model.addAttribute("route",      new Route());
        model.addAttribute("formAction", "Add");
        return "route-form";
    }

    @PostMapping("/routes/save")
    public String saveRoute(
            @Valid @ModelAttribute Route route,
            BindingResult result,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session)) return "redirect:/admin/login";

        if (result.hasErrors()) {
            model.addAttribute("formAction", "Add");
            return "route-form";
        }

        if (routeService.routeNumberExists(route.getRouteNumber())) {
            model.addAttribute("error",      "Route number '" + route.getRouteNumber() + "' already exists");
            model.addAttribute("formAction", "Add");
            return "route-form";
        }

        routeService.saveRoute(route, (String) session.getAttribute("adminUsername"));
        redirectAttributes.addFlashAttribute("success", "Route added successfully");
        return "redirect:/admin/dashboard";
    }

    // ── EDIT ROUTE ────────────────────────────────────────────────────────────

    @GetMapping("/routes/edit/{id}")
    public String showEditForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/admin/login";
        model.addAttribute("route",      routeService.getRouteById(id));
        model.addAttribute("formAction", "Edit");
        return "route-form";
    }

    @PostMapping("/routes/update")
    public String updateRoute(
            @Valid @ModelAttribute Route route,
            BindingResult result,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session)) return "redirect:/admin/login";

        if (result.hasErrors()) {
            model.addAttribute("formAction", "Edit");
            return "route-form";
        }

        routeService.updateRoute(route, (String) session.getAttribute("adminUsername"));
        redirectAttributes.addFlashAttribute("success", "Route updated successfully");
        return "redirect:/admin/dashboard";
    }

    // ── DEACTIVATE ROUTE ──────────────────────────────────────────────────────

    @PostMapping("/routes/delete/{id}")
    public String deleteRoute(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session)) return "redirect:/admin/login";

        routeService.deleteRoute(id, (String) session.getAttribute("adminUsername"));
        redirectAttributes.addFlashAttribute("success", "Route deactivated successfully");
        return "redirect:/admin/dashboard";
    }

    // ── REACTIVATE ROUTE ──────────────────────────────────────────────────────

    @PostMapping("/routes/reactivate/{id}")
    public String reactivateRoute(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session)) return "redirect:/admin/login";

        routeService.reactivateRoute(id, (String) session.getAttribute("adminUsername"));
        redirectAttributes.addFlashAttribute("success", "Route reactivated successfully");
        return "redirect:/admin/dashboard";
    }

    // ── CSV EXPORT ────────────────────────────────────────────────────────────

    @GetMapping("/routes/export")
    public void exportCsv(HttpSession session, HttpServletResponse response) throws IOException {
        if (!isLoggedIn(session)) {
            response.sendRedirect("/admin/login");
            return;
        }

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"daladala-routes.csv\"");

        PrintWriter writer = response.getWriter();
        writer.println("Route Number,From,To,Via Stops,Distance (km),Fare (TZS),Status");

        for (Route r : routeService.getAllRoutes()) {
            writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",%.1f,%d,\"%s\"%n",
                    r.getRouteNumber(),
                    r.getStartPoint(),
                    r.getEndPoint(),
                    r.getViaStops() != null ? r.getViaStops() : "",
                    r.getDistanceKm(),
                    r.getFareTzs(),
                    Boolean.TRUE.equals(r.getIsActive()) ? "Active" : "Inactive"
            );
        }
        writer.flush();
    }

    // ── CHANGE PASSWORD ───────────────────────────────────────────────────────

    @GetMapping("/change-password")
    public String changePasswordPage(HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/admin/login";
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session)) return "redirect:/admin/login";

        String username = (String) session.getAttribute("adminUsername");
        Admin admin = adminRepository.findByUsername(username).orElseThrow();

        if (!admin.getPassword().equals(oldPassword)) {
            model.addAttribute("error", "Current password is incorrect");
            return "change-password";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match");
            return "change-password";
        }
        if (newPassword.trim().length() < 4) {
            model.addAttribute("error", "New password must be at least 4 characters");
            return "change-password";
        }

        admin.setPassword(newPassword);
        adminRepository.save(admin);

        redirectAttributes.addFlashAttribute("success", "Password changed successfully");
        return "redirect:/admin/dashboard";
    }

    // ── HELPER ────────────────────────────────────────────────────────────────

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("adminUsername") != null;
    }
}
