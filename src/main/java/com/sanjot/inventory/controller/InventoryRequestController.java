package com.sanjot.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.sanjot.inventory.services.InventoryRequestService;

@Controller
@RequestMapping("/requests")
public class InventoryRequestController {

    @Autowired
    private InventoryRequestService inventoryRequestService;

    @GetMapping
    public String listRequests(Model model) {
        model.addAttribute("requests", inventoryRequestService.getPendingRequests());
        return "inventory_requests";
    }

    @PostMapping("/approve/{id}")
    public String approveRequest(@PathVariable Long id) {
        inventoryRequestService.approveRequest(id);
        return "redirect:/requests";
    }

    @PostMapping("/deny/{id}")
    public String denyRequest(@PathVariable Long id) {
        inventoryRequestService.denyRequest(id);
        return "redirect:/requests";
    }
    
}
