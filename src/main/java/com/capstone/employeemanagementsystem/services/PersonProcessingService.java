package com.capstone.employeemanagementsystem.services;

import com.capstone.employeemanagementsystem.dto.EmployeeDto;
import com.capstone.employeemanagementsystem.dto.NewEmployeeDto;
import com.capstone.employeemanagementsystem.models.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

interface PersonProcessingService {
    ResponseEntity<?> generateReport(int select, Pageable pageable, Long id);

    ResponseEntity<?> generateReport(String department, Pageable pageable);

    ResponseEntity<?> generateReport(Integer age, Pageable pageable);

    ResponseEntity<?> cdOps(String select, NewEmployeeDto employeeDto);
}
