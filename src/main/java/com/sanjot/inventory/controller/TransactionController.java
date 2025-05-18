package com.sanjot.inventory.controller;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.sanjot.inventory.entity.InventoryItem;
import com.sanjot.inventory.entity.Transaction;
import com.sanjot.inventory.repository.InventoryItemRepository;
import com.sanjot.inventory.repository.TransactionRepository;
import com.sanjot.inventory.services.EmailService;
import com.sanjot.inventory.services.InventoryItemService;
import com.sanjot.inventory.services.TransactionService;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final InventoryItemService inventoryItemService ;
    @Autowired
    private  InventoryItemRepository inventoryItemRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private EmailService emailService;



    public TransactionController(TransactionService transactionService,
    		InventoryItemService inventoryItemService,EmailService emailService) {
        this.transactionService = transactionService;
        this.inventoryItemService=inventoryItemService;
        this.emailService=emailService;
    }
    @GetMapping("/requests")
    public String viewRequests(Model model) {
        List<Transaction> pendingRequests = transactionService.getPendingTransactions();

        model.addAttribute("requests", transactionService.getPendingTransactions());
        return "transaction-requests";
    }

//    @PostMapping("/approve/{id}")
//    public String approveRequest(@PathVariable Long id) {
//        transactionService.approveTransaction(id);
//        return "redirect:/transactions/requests";
//    }
//
//    @PostMapping("/reject/{id}")
//    public String rejectRequest(@PathVariable Long id) {
//        transactionService.rejectRequest(id);
//        return "redirect:/transactions/requests";
//    }
    @GetMapping("/details/{id}")
    public String viewTransactionDetails(@PathVariable Long id, Model model) {
        // Fetch transaction by ID
        Transaction transaction = transactionService.getTransactionById(id);

        if (transaction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found");
        }

        model.addAttribute("transaction", transaction);
        return "request-details";  // points to templates/transactions/details.html
    }

//    @PostMapping("/approve/{id}")
//    public String approveRequest(@PathVariable Long id) {
//        try {
//            transactionService.approveTransaction(id);
//        } catch (IllegalStateException e) {
//            // Optionally handle error and pass message to UI
//            System.err.println("Approval Error: " + e.getMessage());
//        }
//        String staffEmail = transaction.getUser().getEmail();
//        String itemName = transaction.getInventory().getName();
//        emailService.sendRequestStatusEmail(staffEmail, itemName, "APPROVED", request.getRemarks());
//
//        // Flash notification for UI
//        redirectAttributes.addFlashAttribute("notificationMessage", "Inventory request approved and email sent!");
//
//        return "redirect:/transactions/requests";
//    }
//    @PostMapping("/approve/{id}")
//    public String approveRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
//        try {
//            // First, get the transaction details
//            Transaction transaction = transactionService.getTransactionById(id);
//
//            if (transaction == null) {
//                redirectAttributes.addFlashAttribute("notificationMessage", "Transaction not found.");
//                return "redirect:/transactions/requests";
//            }
//
//            // Approve the transaction (the existing service method returns a String)
//            transactionService.approveTransaction(id);
//
//            // Send email notification only if status is APPROVED
//            if (transaction.getStatus().toString().equals("APPROVED")) {
//                String staffEmail = transaction.getUser().getEmail();
//                String itemName = transaction.getInventoryItem().getName();
//                String remarks = transaction.getRemarks();
//
//                emailService.sendRequestStatusEmail(staffEmail, itemName, "APPROVED", remarks);
//                redirectAttributes.addFlashAttribute("notificationMessage", "Inventory request approved and email sent!");
//            } else if (transaction.getStatus().toString().equals("REJECTED")) {
//                // Handle auto-rejection case (e.g., insufficient stock)
//                String staffEmail = transaction.getUser().getEmail();
//                String itemName = transaction.getInventoryItem().getName();
//                String remarks = transaction.getRemarks();
//
//                emailService.sendRequestStatusEmail(staffEmail, itemName, "REJECTED", remarks);
//                redirectAttributes.addFlashAttribute("notificationMessage", "Request rejected due to insufficient stock. Email sent.");
//            }
//        } catch (IllegalStateException e) {
//            redirectAttributes.addFlashAttribute("notificationMessage", "Error approving request: " + e.getMessage());
//        }
//
//        return "redirect:/transactions/requests";
//    }
//    @PostMapping("/approve/{id}")
//    public String approveRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
//        try {
//            // First, get the transaction details
//            Transaction transaction = transactionService.getTransactionById(id);
//
//            if (transaction == null) {
//                redirectAttributes.addFlashAttribute("notificationMessage", "Transaction not found.");
//                return "redirect:/transactions/requests";
//            }
//
//            // ✅ Handle return request approval (injected logic)
//            if (transaction.getTransactionType() == Transaction.TransactionType.ADD
//                    && transaction.getStatus() == Transaction.TransactionStatus.PENDING) {
//
//                InventoryItem item = transaction.getInventoryItem();
//                item.setQuantity(item.getQuantity() + transaction.getQuantity());
//                inventoryItemRepository.save(item);
//
//                transaction.setStatus(Transaction.TransactionStatus.APPROVED);
//                transaction.setTransactionDate(LocalDateTime.now());
//                transactionRepository.save(transaction);
//            }
//
//            // ✅ Now call your service logic to handle general approval flow
//            transactionService.approveTransaction(id);
//
//            // ✅ Send email notification based on status
//            if (transaction.getStatus() == Transaction.TransactionStatus.APPROVED) {
//                String staffEmail = transaction.getUser().getEmail();
//                String itemName = transaction.getInventoryItem().getName();
//                String remarks = transaction.getRemarks();
//
//                emailService.sendRequestStatusEmail(staffEmail, itemName, "APPROVED", remarks);
//                redirectAttributes.addFlashAttribute("notificationMessage", "Inventory request approved and email sent!");
//            } else if (transaction.getStatus() == Transaction.TransactionStatus.REJECTED) {
//                String staffEmail = transaction.getUser().getEmail();
//                String itemName = transaction.getInventoryItem().getName();
//                String remarks = transaction.getRemarks();
//
//                emailService.sendRequestStatusEmail(staffEmail, itemName, "REJECTED", remarks);
//                redirectAttributes.addFlashAttribute("notificationMessage", "Request rejected due to insufficient stock. Email sent.");
//            }
//
//        } catch (IllegalStateException e) {
//            redirectAttributes.addFlashAttribute("notificationMessage", "Error approving request: " + e.getMessage());
//        }
//
//        return "redirect:/transactions/requests";
//    }
    @PostMapping("/approve/{id}")
    public String approveRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Transaction transaction = transactionService.getTransactionById(id);
            if (transaction == null) {
                redirectAttributes.addFlashAttribute("notificationMessage", "Transaction not found.");
                return "redirect:/transactions/requests";
            }

            // ✅ Call unified service method
            String statusMessage = transactionService.approveTransaction(id);

            // ✅ Send email notification
            String staffEmail = transaction.getUser().getEmail();
            String itemName = transaction.getInventoryItem().getName();
            String remarks = transaction.getRemarks();

            emailService.sendRequestStatusEmail(staffEmail, itemName, transaction.getStatus().toString(), remarks);
            redirectAttributes.addFlashAttribute("notificationMessage", statusMessage + " Email sent!");

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("notificationMessage", "Error approving request: " + e.getMessage());
        }

        return "redirect:/transactions/requests";
    }


//
//    @PostMapping("/reject/{id}")
//    public String rejectRequest(@PathVariable Long id) {
//        transactionService.rejectRequest(id);
//        return "redirect:/transactions/requests";
//    }
    @PostMapping("/reject/{id}")
    public String rejectRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // ✅ Step 1: Fetch transaction from the DB
        Transaction transaction = transactionService.getTransactionById(id);

        if (transaction == null) {
            redirectAttributes.addFlashAttribute("notificationMessage", "Transaction not found.");
            return "redirect:/transactions/requests";
        }

        // ✅ Step 2: Reject the transaction
        transactionService.rejectRequest(id);

        try {
            // ✅ Step 3: Extract email info
            String staffEmail = transaction.getUser().getEmail();
            String itemName = transaction.getInventoryItem().getName();
            String remarks = transaction.getRemarks();

            // ✅ Step 4: Send the email
            emailService.sendRequestStatusEmail(staffEmail, itemName, "REJECTED", remarks);
            redirectAttributes.addFlashAttribute("notificationMessage", "Request rejected and email sent.");
        } catch (Exception e) {
            System.err.println("Error sending rejection email: " + e.getMessage());
            redirectAttributes.addFlashAttribute("notificationMessage", "Request rejected, but email failed to send.");
        }

        return "redirect:/transactions/requests";
    }


}
