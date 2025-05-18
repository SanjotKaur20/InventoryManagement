package com.sanjot.inventory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/ManageInventory")
    public String showManageInventoryPage() {
        return "ManageInventory";  // Ensure "manageInventory.html" exists in templates folder
    }

    @GetMapping("/")
    public String HomePage() {
        return "home";      }
    @GetMapping("/ContactUs")
    public String ContactPage() {
        return "ContactUs";      }
    @GetMapping("/AboutUs")
    public String AboutPage() {
        return "AboutUs";      }






}
