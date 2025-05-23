package com.sanjot.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanjot.inventory.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
