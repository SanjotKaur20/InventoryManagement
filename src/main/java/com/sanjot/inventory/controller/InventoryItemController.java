package com.sanjot.inventory.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sanjot.inventory.entity.Category;
import com.sanjot.inventory.entity.InventoryItem;
import com.sanjot.inventory.entity.InventoryRequest;
import com.sanjot.inventory.entity.Transaction;
import com.sanjot.inventory.entity.Transaction.TransactionType;
import com.sanjot.inventory.entity.User;
import com.sanjot.inventory.repository.InventoryItemRepository;
import com.sanjot.inventory.repository.InventoryRequestRepository;
import com.sanjot.inventory.repository.TransactionRepository;
import com.sanjot.inventory.repository.UserRepository;
import com.sanjot.inventory.services.CategoryService;
import com.sanjot.inventory.services.InventoryItemService;
import com.sanjot.inventory.services.TransactionService;
import com.sanjot.inventory.services.UserService;



@Controller  // Handles Thymeleaf views
@RequestMapping("/inventory")
public class InventoryItemController {

    @Autowired
    private InventoryItemService inventoryItemService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private InventoryRequestRepository inventoryRequestRepository;




    
    private  InventoryItem inventoryItem;

    @GetMapping
    public String showInventoryList(Model model) {
        List<InventoryItem> inventoryList = inventoryItemService.getAllItems();
        
        // Debugging: Print to console
        System.out.println("Fetched Inventory Items: " + inventoryList.size());
        for (InventoryItem item : inventoryList) {
            System.out.println("Item: " + item.getName());
        }

        model.addAttribute("inventoryList", inventoryList);
        model.addAttribute("categories", categoryService.getAllCategories());

        return "ManageInventory";
    }
    @GetMapping("/inventory-request")
    public String showRequestPage(Model model,Principal principal) {
        System.out.println("Logged-in user: " + principal.getName());
        // Check username
        List<Transaction> pendingRequests = transactionService.getPendingRequests(); // Fetch pending transactions
        model.addAttribute("requests", pendingRequests);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Roles: " + auth.getAuthorities()); // Check user roles
        if (auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_STAFF"))) {
            System.out.println("User lacks required roles! Blocking access.");
            return "error/403"; // Show a custom error page
        }

        List<InventoryItem> inventoryItems = inventoryItemService.getAllItems(); // Fetch inventory items
        model.addAttribute("inventoryItems", inventoryItems); // Add to model

        return "inventory-request"; // Must match the Thymeleaf template name
    }
//    @PostMapping("/inventory-request")
//    public String submitInventoryRequest(@ModelAttribute Transaction transaction, Authentication authentication) {
//        
//    	transactionService.saveTransaction(transaction, authentication);
//        return "redirect:/inventory/request?success";
//    }
//    @PostMapping("/inventory-request")
//    public ResponseEntity<String> submitInventoryRequest(@RequestBody Transaction transaction,Authentication authentication) {
//        if (transaction.getTransactionType() == null || transaction.getTransactionType().name().isEmpty()) { // ✅ Check for empty string
//
//            return ResponseEntity.badRequest().body("Transaction type cannot be null or empty");
//        }
//
//        transactionService.saveTransaction(transaction,authentication);
//        return ResponseEntity.ok("Transaction saved successfully");
//    }
//
    @PostMapping("/inventory-request")
    public String submitInventoryRequest(
            @RequestParam("inventoryId") Long inventoryId,  // ✅ Fetch inventoryId separately
            @RequestParam("transactionType") Transaction.TransactionType transactionType,
            @RequestParam("quantity") int quantity,
            @RequestParam("destination") String destination,
            @RequestParam(value = "remarks", required = false) String remarks,
            Authentication authentication) {
    	System.out.println("✅ Received inventoryId: " + inventoryId);
        System.out.println("✅ Received transactionType: " + transactionType);
        System.out.println("✅ Received quantity: " + quantity);
        System.out.println("✅ Received destination: " + destination);
        System.out.println("✅ Received remarks: " + remarks);


        // Fetch InventoryItem
        InventoryItem inventoryItem = inventoryItemService.getItemById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Inventory ID"));

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername(); // ✅ Get email from authentication
        } else {
            email = principal.toString();
        }

        // ✅ Fetch the user entity from DB using email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        System.out.println("✅ Authenticated User: " + user.getEmail());


        // Create and set transaction
        Transaction transaction = new Transaction();
        transaction.setInventoryItem(inventoryItem);  // ✅ Set inventoryItem manually
        transaction.setUser(user);
        transaction.setTransactionType(transactionType);
        transaction.setQuantity(quantity);
        transaction.setDestination(destination);
        transaction.setRemarks(remarks);

        // Save transaction
        transactionService.saveTransaction(transaction,authentication);

        return "redirect:/inventory/inventory-request?success";
    }
    @GetMapping("/add")
    public String showAddInventoryForm(Model model) {
        InventoryItem inventoryItem = new InventoryItem();  // ✅ Initialize before using
        model.addAttribute("inventoryItem", inventoryItem);
        model.addAttribute("categories", categoryService.getAllCategories()); // Pass categories

        return "add-inventory"; // Thymeleaf template
    }

//    @PostMapping("/save")
//    public String saveInventory(@ModelAttribute InventoryItem inventoryItem, @RequestParam("categoryId") Long categoryId) {
//        Category category = categoryService.getCategoryById(categoryId);
//        if (category != null) {
//            inventoryItem.setCategory(category); // ✅ Directly assign category
//        }
//
//        inventoryItemService.saveItem(inventoryItem);
//        return "redirect:/inventory"; // Redirects to inventory list
//    }
    @PostMapping("/save")
    public String saveInventory(@ModelAttribute InventoryItem inventoryItem, @RequestParam("categoryId") Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        if (category != null) {
            inventoryItem.setCategory(category); // ✅ Assign category
        }

        if (inventoryItem.getId() != null) { // ✅ If ID exists, update the record
            Optional<InventoryItem> existingItem = inventoryItemService.getItemById(inventoryItem.getId());
            if (existingItem.isPresent()) {
                InventoryItem updatedItem = existingItem.get();
                updatedItem.setName(inventoryItem.getName());
                updatedItem.setQuantity(inventoryItem.getQuantity());
                updatedItem.setUnitPrice(inventoryItem.getUnitPrice());
                updatedItem.setStatus(inventoryItem.getStatus());
                updatedItem.setCategory(category);
                inventoryItemService.saveItem(updatedItem); // ✅ Update existing item
            }
        } else {
            inventoryItemService.saveItem(inventoryItem); // ✅ Insert new item if ID is null
        }

        return "redirect:/inventory"; // Redirects to inventory list
    }

    @GetMapping("/delete/{id}")
    public String deleteInventoryItem(@PathVariable Long id) {
        System.out.println("Deleting item with ID: " + id);  // Debugging
        inventoryItemService.deleteItem(id);
        return "redirect:/inventory"; // Redirect to inventory list after deletion
    }
    @GetMapping("/edit/{id}")
    public String showEditInventoryForm(@PathVariable Long id, Model model) {
        Optional<InventoryItem> inventoryItemOptional = inventoryItemService.getItemById(id);
        
        if (inventoryItemOptional.isPresent()) {
            InventoryItem inventoryItem = inventoryItemOptional.get();
            model.addAttribute("inventoryItem", inventoryItem);
            model.addAttribute("categories", categoryService.getAllCategories()); // ✅ Load categories properly
            model.addAttribute("selectedCategoryId", inventoryItem.getCategory() != null ? inventoryItem.getCategory().getId() : null);
        } else {
            return "redirect:/inventory";  // Redirect if item not found
        }

        return "add-inventory"; // ✅ Load the same template for editing
    }
    @GetMapping("/return_request")
    public String showIssuedItems(Model model, Principal principal) {
        String email = principal.getName();

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        List<Transaction> issuedItems = transactionRepository.findByUserId(user.getId());
//        List<Transaction> itemsToDisplay = issuedItems.stream()
//                .filter(item -> !item.getDestination().equalsIgnoreCase("RETURN_REQUEST"))
//                .collect(Collectors.toList());

        model.addAttribute("issuedItems", issuedItems);
        return "return_request";
    }
    @PostMapping("/request-return")
    public String requestReturn(@RequestParam("inventoryId") Long transactionId,
                                @RequestParam("quantity") int quantity,
                                Authentication authentication) {

        // ✅ Get the current user's email
        Object principal = authentication.getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Find the original issued transaction
        Transaction issuedTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // ✅ Create a new Transaction as a return request
        Transaction returnTransaction = new Transaction();
        returnTransaction.setInventoryItem(issuedTransaction.getInventoryItem());
        returnTransaction.setUser(user);
        returnTransaction.setTransactionType(Transaction.TransactionType.ADD); // or RETURN if enum supports it
        returnTransaction.setQuantity(quantity);
        returnTransaction.setDestination("RETURN_REQUEST"); // Optional field for clarity
        returnTransaction.setRemarks("Return Request for issued item");

        // ✅ Save return transaction
        transactionService.saveTransaction(returnTransaction, authentication);

        return "redirect:/inventory/return_request?success";
    }

    }




