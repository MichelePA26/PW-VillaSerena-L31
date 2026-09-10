package com.villaserena.museo.controller;

import com.villaserena.museo.dto.DashboardDTO;
import com.villaserena.museo.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATORE','HR')")
    public DashboardDTO get() {
        return dashboardService.calcola();
    }
}