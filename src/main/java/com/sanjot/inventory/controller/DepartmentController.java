package com.sanjot.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.sanjot.inventory.entity.Department;
import com.sanjot.inventory.services.DepartmentService;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public String listDepartments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("department", new Department()); // Ensure the form gets a blank Department object
        return "departments";
    }

    @PostMapping("/add")
    public String addDepartment(@ModelAttribute Department department) {
        if (department.getName() == null || department.getName().trim().isEmpty()) {
            return "redirect:/departments?error=NameRequired";
        }
        departmentService.saveDepartment(department);
        return "redirect:/departments";
    }

    @GetMapping("/delete/{id}")
    public String deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return "redirect:/departments";
    }
}
