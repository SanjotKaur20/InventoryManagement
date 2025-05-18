package com.sanjot.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanjot.inventory.entity.InventoryRequest;
import com.sanjot.inventory.entity.Transaction;

import java.util.List;

@Repository
public interface InventoryRequestRepository extends JpaRepository<InventoryRequest, Long> {
    List<InventoryRequest> findByStatus(InventoryRequest.RequestStatus status);
}
