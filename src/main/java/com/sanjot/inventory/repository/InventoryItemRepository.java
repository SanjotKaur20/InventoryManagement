package com.sanjot.inventory.repository;

import com.sanjot.inventory.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByStatus(InventoryItem.Status status);

    void deleteById(Long id);

}
