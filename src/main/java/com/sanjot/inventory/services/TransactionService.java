package com.sanjot.inventory.services;

import com.sanjot.inventory.entity.InventoryItem;
import com.sanjot.inventory.entity.Transaction;
import com.sanjot.inventory.entity.Transaction.TransactionStatus;
import com.sanjot.inventory.entity.Transaction.TransactionType;
import com.sanjot.inventory.entity.User;
import com.sanjot.inventory.repository.InventoryItemRepository;
import com.sanjot.inventory.repository.TransactionRepository;
import com.sanjot.inventory.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final InventoryItemRepository inventoryRepository;
    private final UserRepository userRepository;


    public TransactionService(TransactionRepository transactionRepository,
    		InventoryItemRepository inventoryRepository,UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository=userRepository;
    }

    // ✅ Get all transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // ✅ Get a single transaction by ID
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }
    public List<Transaction> getAddTransactions() {
        return transactionRepository.findByTransactionType(Transaction.TransactionType.ADD);
    }
//    public void saveTransaction(Transaction transaction, Authentication authentication) {
//        // ✅ Get logged-in user
//        String email = authentication.getName();
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        transaction.setUser(user); // ✅ Set user instead of createdBy
//        transactionRepository.save(transaction);
//    }
    public void saveTransaction(Transaction transaction, Authentication authentication) {
        // ✅ Get logged-in user
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        transaction.setUser(user); // ✅ Set user instead of createdBy

        // ✅ Modify inventory only if transaction is APPROVED
//        if (TransactionStatus.APPROVED.equals(transaction.getStatus())) {
//            InventoryItem item = transaction.getInventoryItem();
//
//            if (transaction.getTransactionType() == Transaction.TransactionType.ADD) {
//                item.setQuantity(item.getQuantity() + transaction.getQuantity());
//            } else if (transaction.getTransactionType() == Transaction.TransactionType.REMOVE) {
//                item.setQuantity(item.getQuantity() - transaction.getQuantity());
//            }
//
//            inventoryRepository.save(item);
//        }
        if (user.getDepartment() != null) {
            transaction.setDepartment(user.getDepartment());
        }
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    public Transaction findById(Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        return transaction.orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + id));
    }


    // ✅ Delete a transaction
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
    public List<Transaction> getPendingRequests() {
        return transactionRepository.findByTransactionTypeAndRemarksIsNull(TransactionType.REMOVE);
    }
    public List<Transaction> getPendingTransactions() {
        return transactionRepository.findByStatus(TransactionStatus.PENDING);
    }
    public int getPendingRequestCount() {
        return transactionRepository.countByStatus("PENDING");
    }


    // 🔹 New Feature: Approve an inventory removal request
    public String approveTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Transaction not found"));
        InventoryItem item = transaction.getInventoryItem();

        int currentQty = item.getQuantity();
        int requestedQty = transaction.getQuantity();

        if (transaction.getTransactionType() == TransactionType.REMOVE) {
            if (currentQty >= requestedQty) {
                item.setQuantity(currentQty - requestedQty);
                transaction.setStatus(TransactionStatus.APPROVED);
                transaction.setRemarks("Approved by Admin");
                inventoryRepository.save(item);
                transactionRepository.save(transaction);
                return "Request approved";
            } else {
                transaction.setStatus(TransactionStatus.REJECTED);
                transaction.setRemarks("Rejected: Insufficient stock");
                transactionRepository.save(transaction);
                return "Insufficient stock. Request rejected.";
            }
        } else if (transaction.getTransactionType() == TransactionType.ADD) {
            item.setQuantity(currentQty + requestedQty);
            transaction.setStatus(TransactionStatus.APPROVED);
            
            transaction.setRemarks("Approved by Admin");
            //transaction.setReturned(true); // Mark return as complete
         // Update original REMOVE transaction's isReturned = true
            Transaction originalRemove = transactionRepository.findTopByInventoryItemIdAndUserIdAndTransactionTypeAndQuantityAndIsReturnedFalse(
                transaction.getInventoryItem().getId(),
                transaction.getUser().getId(),
                TransactionType.REMOVE,
                transaction.getQuantity()
            );

            if (originalRemove != null) {
                originalRemove.setReturned(true);
                transactionRepository.save(originalRemove);
            }

            inventoryRepository.save(item);
            
            transactionRepository.save(transaction);

            
            return "Stock added successfully.";
        }

        return "Unknown error";
    }

//    public void rejectRequest(Long id) {
//        Transaction transaction = transactionRepository.findById(id)
//                .orElseThrow(() -> new IllegalStateException("Transaction not found"));
//        transaction.setStatus(Transaction.TransactionStatus.REJECTED);
//        transaction.setRemarks("Rejected by Admin");
//        transactionRepository.save(transaction);
//    }
    public void rejectRequest(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Transaction not found"));

        if (transaction.getStatus() != TransactionStatus.APPROVED) {
            transaction.setStatus(TransactionStatus.REJECTED);
            transaction.setRemarks("Rejected by Admin");
            transactionRepository.save(transaction);
        } else {
            throw new IllegalStateException("Cannot reject an already approved transaction");
        }
    }

    public void rejectTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Transaction not found"));
        
        transaction.setRemarks("Rejected");
        transactionRepository.save(transaction);
    }

}
