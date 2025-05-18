package com.sanjot.inventory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import com.sanjot.inventory.entity.Category;
import com.sanjot.inventory.services.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService; // Inject the CategoryService

    // ✅ Show Category List Page
    @GetMapping("/categories")
    public String showCategoryPage(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories";
    }

    // ✅ Show Add/Edit Category Form
    @GetMapping("/add")
    public String showAddCategoryPage(@RequestParam(value = "id", required = false) Long id, Model model) {
        if (id != null) {
            // If ID exists, fetch category for editing
            Category category = categoryService.getCategoryById(id);
            model.addAttribute("category", category);
        } else {
            // Otherwise, create a new category
            model.addAttribute("category", new Category());
        }
        return "add-category"; // Same form for add and edit
    }

    // ✅ Handle Add & Update Category in One Method
    @PostMapping("/categories/save")
    public String saveCategory(@Valid @ModelAttribute("category") Category category, BindingResult result) {
        if (result.hasErrors()) {
            return "add-category"; // Return back to the form if validation fails
        }
        categoryService.saveCategory(category); // Save (new or updated)
        return "redirect:/categories"; // Redirect to categories list
    }

    // ✅ Delete Category
    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories";
    }
}
