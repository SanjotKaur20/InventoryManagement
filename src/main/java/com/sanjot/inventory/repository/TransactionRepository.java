package com.sanjot.inventory.repository;

import com.sanjot.inventory.entity.InventoryItem;
import com.sanjot.inventory.entity.Transaction.TransactionStatus;

import com.sanjot.inventory.entity.Transaction;
import com.sanjot.inventory.entity.Transaction.TransactionType;
import com.sanjot.inventory.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByTransactionTypeAndRemarksIsNull(Transaction.TransactionType transactionType);
    List<Transaction> findByTransactionType(Transaction.TransactionType transactionType);
    List<Transaction> findByStatus(TransactionStatus status);
    List<Transaction> findByDepartmentId(Long departmentId);
    @Query("SELECT t FROM Transaction t " +
    	    "WHERE t.user.department.id = :departmentId " +
 	       "AND t.status = 'APPROVED' " +
 	      "AND t.transactionType = 'REMOVE' " +
	       "AND t.destination != 'RETURN_REQUEST' " +
	       "AND (t.isReturned = false OR t.isReturned IS NULL)"
// 	      + "AND t.returned = false"
 	       )
 	List<Transaction> findApprovedRemovedNotReturnedByDepartment(@Param("departmentId") Long departmentId);
    int countByStatus(TransactionStatus status);

    @Query("SELECT t FROM Transaction t " +
    	       "WHERE t.status = 'APPROVED' " +
    	       "AND t.transactionType = 'REMOVE' " +
    	       "AND t.destination != 'RETURN_REQUEST' " +
    	       "AND (t.isReturned = false OR t.isReturned IS NULL)"
    	       )
    	List<Transaction> findAllApprovedRemovedNotReturned();

    @Query("SELECT t FROM Transaction t " +
    	       "WHERE t.inventoryItem.id = :inventoryId " +
    	       "AND t.user.id = :userId " +
    	       "AND t.transactionType = 'REMOVE' " +
    	       "AND t.quantity = :quantity " +
//    	       "AND t.isReturned = false " +
    	       "AND t.status = 'APPROVED'")
    	Transaction findTopByInventoryItemIdAndUserIdAndTransactionTypeAndQuantityAndIsReturnedFalse(
    	    Long inventoryId, Long userId, TransactionType transactionType, int quantity);

// ✅ Fetch transactions by status
    int countByStatus(String status);

    void deleteByInventoryItem(InventoryItem inventoryItem);
    @Query("SELECT t.inventoryItem FROM Transaction t WHERE t.user.id = :userId AND t.transactionType = 'REMOVE'")
    List<InventoryItem> findIssuedItemsByUserId(@Param("userId") Long userId);
    List<Transaction> findByUserId(Long userId);


    
}
