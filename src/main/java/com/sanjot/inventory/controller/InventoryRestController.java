// Separate REST API Controller
package com.sanjot.inventory.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanjot.inventory.entity.InventoryItem;
import com.sanjot.inventory.services.InventoryItemService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryRestController {

    @Autowired
    private InventoryItemService inventoryItemService;

    // Get All Inventory Items (REST API)
    @GetMapping
    public List<InventoryItem> getAllItems() {
        return inventoryItemService.getAllItems();
    }

    // Get Inventory Item by ID (REST API)
    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> getItemById(@PathVariable Long id) {
        Optional<InventoryItem> item = inventoryItemService.getItemById(id);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create New Inventory Item (REST API)
    @PostMapping
    public InventoryItem createItem(@RequestBody InventoryItem item) {
        return inventoryItemService.saveItem(item);
    }

    // Delete Inventory Item (REST API)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        inventoryItemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
