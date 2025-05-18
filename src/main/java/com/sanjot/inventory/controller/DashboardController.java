package com.sanjot.inventory.controller;


import com.sanjot.inventory.services.TransactionService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;


@Controller
public class DashboardController {
    @Autowired
    private TransactionService transactionService;


    @GetMapping("/admin/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "admin_dashboard"; // Match with Thymeleaf file name
    }

    @GetMapping("/staff/dashboard")
    public String staffDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("username", authentication.getName());

        int pendingCount = transactionService.getPendingRequestCount();
        model.addAttribute("pendingCount", pendingCount);

        return "staff_dashboard"; // Match with Thymeleaf file name
    }
}
