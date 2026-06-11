package com.daladala.controller;

import com.daladala.entity.Admin;
import com.daladala.entity.Route;
import com.daladala.repository.AdminRepository;
import com.daladala.service.RouteHistoryService;
import com.daladala.service.RouteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RouteService routeService;
    private final RouteHistoryService routeHistoryService;
    private final AdminRepository adminRepository;  // login check only — no AdminService needed

    // ──────────────────────────────────────────────
    // LOGIN
    // ──────────────────────────────────────────────

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

    // ──────────────────────────────────────────────
    // DASHBOARD
    // ──────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/admin/login";

        model.addAttribute("routes", routeService.getAllRoutes());
        model.addAttribute("recentHistory", routeHistoryService.getAllHistory());
        return "admin-dashboard";
    }

    // ──────────────────────────────────────────────
    // ADD ROUTE
    // ──────────────────────────────────────────────

    @GetMapping("/routes/new")
    public String showAddForm(HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/admin/login";

        model.addAttribute("route", new Route());
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
            model.addAttribute("error", "Route number '" + route.getRouteNumber() + "' already exists");
            model.addAttribute("formAction", "Add");
            return "route-form";
        }

        String adminUsername = (String) session.getAttribute("adminUsername");
        routeService.saveRoute(route, adminUsername);
        redirectAttributes.addFlashAttribute("success", "Route added successfully");
        return "redirect:/admin/dashboard";
    }

    // ──────────────────────────────────────────────
    // EDIT ROUTE
    // ──────────────────────────────────────────────

    @GetMapping("/routes/edit/{id}")
    public String showEditForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/admin/login";

        model.addAttribute("route", routeService.getRouteById(id));
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

        String adminUsername = (String) session.getAttribute("adminUsername");
        routeService.updateRoute(route, adminUsername);
        redirectAttributes.addFlashAttribute("success", "Route updated successfully");
        return "redirect:/admin/dashboard";
    }

    // ──────────────────────────────────────────────
    // DELETE ROUTE
    // ──────────────────────────────────────────────

    // POST not GET — prevents accidental deletion via a URL in the browser
    @PostMapping("/routes/delete/{id}")
    public String deleteRoute(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session)) return "redirect:/admin/login";

        String adminUsername = (String) session.getAttribute("adminUsername");
        routeService.deleteRoute(id, adminUsername);
        redirectAttributes.addFlashAttribute("success", "Route deactivated successfully");
        return "redirect:/admin/dashboard";
    }

    // ──────────────────────────────────────────────
    // HELPER
    // ──────────────────────────────────────────────

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("adminUsername") != null;
    }
}
