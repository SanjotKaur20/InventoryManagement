package com.sanjot.inventory.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sanjot.inventory.entity.InventoryRequest;
import com.sanjot.inventory.repository.InventoryRequestRepository;

import java.util.Date;
import java.util.List;

@Service
public class InventoryRequestService {

    @Autowired
    private InventoryRequestRepository inventoryRequestRepository;

    public List<InventoryRequest> getPendingRequests() {
        return inventoryRequestRepository.findByStatus(InventoryRequest.RequestStatus.PENDING);
    }

    public void approveRequest(Long requestId) {
        InventoryRequest request = inventoryRequestRepository.findById(requestId).orElse(null);
        if (request != null) {
            request.setStatus(InventoryRequest.RequestStatus.APPROVED);
            request.setApprovedDate(new Date());
            inventoryRequestRepository.save(request);
        }
    }

    public void denyRequest(Long requestId) {
        InventoryRequest request = inventoryRequestRepository.findById(requestId).orElse(null);
        if (request != null) {
            request.setStatus(InventoryRequest.RequestStatus.DENIED);
            inventoryRequestRepository.save(request);
        }
    }
}
