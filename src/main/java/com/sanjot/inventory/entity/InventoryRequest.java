package com.sanjot.inventory.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "inventory_requests")
public class InventoryRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    private InventoryItem inventoryItem;

    @ManyToOne
    private User user;


    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date approvedDate;

    public enum RequestStatus { PENDING, APPROVED, DENIED }

	public InventoryRequest(Long id, Department department, InventoryItem inventoryItem, int quantity,
			RequestStatus status, Date requestDate, Date approvedDate) {
		super();
		this.id = id;
		this.department = department;
		this.inventoryItem = inventoryItem;
		this.quantity = quantity;
		this.status = status;
		this.requestDate = requestDate;
		this.approvedDate = approvedDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public RequestStatus getStatus() {
		return status;
	}

	public void setStatus(RequestStatus status) {
		this.status = status;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	@Override
	public String toString() {
		return "InventoryRequest [id=" + id + ", department=" + department + ", inventoryItem=" + inventoryItem
				+ ", quantity=" + quantity + ", status=" + status + ", requestDate=" + requestDate + ", approvedDate="
				+ approvedDate + "]";
	}
	

}
