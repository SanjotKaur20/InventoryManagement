package com.sanjot.inventory.repository;

import com.sanjot.inventory.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
}
