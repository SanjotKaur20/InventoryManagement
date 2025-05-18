package com.sanjot.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import com.sanjot.inventory.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email); // Add this method
//    Optional<User> findByUsername(String username);

//    boolean existsByEmail(String email); 
    List<User> findByActiveTrue();
    List<User> findByRole(String role);
    Optional<User> findByUsername(String email);


}
