package com.capstone.employeemanagementsystem.repositories;

import com.capstone.employeemanagementsystem.models.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {

    Optional<Manager> findManagerByEmployeeId(String employeeId);

}
