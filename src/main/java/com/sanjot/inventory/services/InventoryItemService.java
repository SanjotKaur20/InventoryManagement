package com.sanjot.inventory.services;

import com.sanjot.inventory.entity.InventoryItem;
import com.sanjot.inventory.entity.User;
import com.sanjot.inventory.repository.CategoryRepository;
import com.sanjot.inventory.repository.InventoryItemRepository;
import com.sanjot.inventory.repository.TransactionRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryItemService {
    
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
     
    @Autowired
    private  UserService userService;
    
    @Autowired
    private TransactionRepository transactionRepository;  // ✅ Inject the missing repository



    public List<InventoryItem> getAllItems() {
        List<InventoryItem> items = inventoryItemRepository.findAll();
        System.out.println("Fetched Inventory Items: " + items); // Debugging print
        return items;
    }
    public Optional<InventoryItem> getItemById(Long id) {
        return inventoryItemRepository.findById(id);
    }

    public InventoryItem saveItem(InventoryItem item) {
        User currentUser = userService.getLoggedInUser(); // Fetch the authenticated user

//        inventoryItemRepository.save(item); // ✅ Hibernate will update if ID exists
        if (currentUser != null) {
            item.setCreatedBy(currentUser); // Set created_by field
        } else {
            throw new RuntimeException("User not authenticated");
        }
        return inventoryItemRepository.save(item); // Return saved entity
    }

    @Transactional
    public void deleteItem(Long id) {
        Optional<InventoryItem> item = inventoryItemRepository.findById(id);
        
        if (item.isPresent()) {
            transactionRepository.deleteByInventoryItem(item.get()); // ✅ First, delete related transactions
            inventoryItemRepository.deleteById(id);  // ✅ Then, delete the inventory item
            System.out.println("Deleted Inventory Item: " + id);
        } else {
            System.out.println("Item not found: " + id);
        }
    }
    
}
