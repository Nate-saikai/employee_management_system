package com.capstone.employeemanagementsystem.controllers;

import com.capstone.employeemanagementsystem.dto.EmployeeRegisterDto;
import com.capstone.employeemanagementsystem.services.EmployeeProcessingService;
import com.capstone.employeemanagementsystem.services.UserRegisterService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/user")
public class EmployeeManagementController {

    private final UserRegisterService userRegisterService;
    private final EmployeeProcessingService employeeProcessingService;

    public EmployeeManagementController(UserRegisterService userRegisterService, EmployeeProcessingService employeeProcessingService) {
        this.userRegisterService = userRegisterService;
        this.employeeProcessingService = employeeProcessingService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNewEmployee(@RequestBody EmployeeRegisterDto employee) {

        try {
            userRegisterService.registerUser(employee);

            return ResponseEntity.ok("Employee added!");
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        catch (NoSuchElementException n) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(n.getMessage());
        }
    }

    @GetMapping("/employees/all")
    public ResponseEntity<?> getAllEmployees(Pageable pageable) {

        return employeeProcessingService.generateReport(1, pageable, 0L);
        // id does nothing here
    }

    @GetMapping("/employee")
    public ResponseEntity<?> getOneEmployee(@RequestParam("id") Long id, Pageable pageable) {

        return employeeProcessingService.generateReport(2, pageable, id);
    }

    @GetMapping("/employees/all/age")
    public ResponseEntity<?> getAllEmployeesByExactAge(@RequestParam("age") Integer age, Pageable pageable) {

        return employeeProcessingService.generateReport(age, pageable);
    }

    @GetMapping("/employees/all/department")
    public ResponseEntity<?> getAllEmployeesByDepartment(@RequestParam("department") String department, Pageable pageable) {

        return employeeProcessingService.generateReport(department, pageable);
    }


}
