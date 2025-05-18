package com.sanjot.inventory.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    private InventoryItem inventoryItem;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String destination;

    private String remarks;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING) // ✅ New column for transaction status
    @Column(nullable = false)
    private TransactionStatus status;

    public enum TransactionType {
        ADD, REMOVE, TRANSFER, MAINTENANCE
    }
//    @ManyToOne
//    @JoinColumn(name = "category_id")
//    private Category category;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;


    public enum TransactionStatus {  
        PENDING, APPROVED, REJECTED  // ✅ Enum for tracking transaction status
    }

    public Transaction() {
        this.transactionDate = LocalDateTime.now(); 
        this.status = TransactionStatus.PENDING;  // ✅ Default status to 'PENDING'
    }

    public Transaction(Long id, InventoryItem inventoryItem, User user, TransactionType transactionType, int quantity, String destination, String remarks, LocalDateTime transactionDate, TransactionStatus status) {
        this.id = id;
        this.inventoryItem = inventoryItem;
        this.user = user;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.destination = destination;
        this.remarks = remarks;
        this.transactionDate = (transactionDate != null) ? transactionDate : LocalDateTime.now();
        this.status = (status != null) ? status : TransactionStatus.PENDING;
    }
    @Column(name = "is_returned")
    private boolean isReturned = false;


//    @Column(nullable = false)
//    private boolean returned = false;
//
//    public boolean isReturned() {
//        return returned;
//    }
//
//    public void setReturned(boolean returned) {
//        this.returned = returned;
//    }


	public boolean isReturned() {
		return isReturned;
	}

	public void setReturned(boolean isReturned) {
		this.isReturned = isReturned;
	}

	// Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public InventoryItem getInventoryItem() { return inventoryItem; }
    public void setInventoryItem(InventoryItem inventoryItem) { this.inventoryItem = inventoryItem; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

	public void setDepartment(Department department) {
		this.department=department;
		// TODO Auto-generated method stub
		
	}
}
