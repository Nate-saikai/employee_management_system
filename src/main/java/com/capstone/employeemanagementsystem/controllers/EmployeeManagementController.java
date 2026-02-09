package com.capstone.employeemanagementsystem.controllers;

import com.capstone.employeemanagementsystem.dto.EmployeeDto;
import com.capstone.employeemanagementsystem.dto.NewEmployeeDto;
import com.capstone.employeemanagementsystem.dto.UpdateEmployeeDto;
import com.capstone.employeemanagementsystem.models.Department;
import com.capstone.employeemanagementsystem.models.Employee;
import com.capstone.employeemanagementsystem.repositories.EmployeeRepository;
import com.capstone.employeemanagementsystem.services.EmployeeProcessingService;
import com.capstone.employeemanagementsystem.services.UserRegisterService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class EmployeeManagementController {

    private final EmployeeRepository employeeRepository;
    private final UserRegisterService userRegisterService;
    private final EmployeeProcessingService employeeProcessingService;

    public EmployeeManagementController(EmployeeRepository employeeRepository, UserRegisterService userRegisterService, EmployeeProcessingService employeeProcessingService) {
        this.employeeRepository = employeeRepository;
        this.userRegisterService = userRegisterService;
        this.employeeProcessingService = employeeProcessingService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNewManager(@RequestBody EmployeeDto manager) {

        try {
            userRegisterService.registerUser(manager);

            return ResponseEntity.ok("Manager added!");
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
        catch (NoSuchElementException n) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", n.getMessage()));
        }
    }

    @GetMapping("/employees/all")
    public ResponseEntity<?> getAllEmployees(Pageable pageable) {

        return employeeProcessingService.generateReport(1, pageable, 0L);
        // id does nothing here
    }

    @GetMapping("/employees/all/age")
    public ResponseEntity<?> getAllEmployeesByExactAge(@RequestParam("age") Integer age, Pageable pageable) {

        return employeeProcessingService.generateReport(age, pageable);
    }

    @GetMapping("/employees/all/department")
    public ResponseEntity<?> getAllEmployeesByDepartment(@RequestParam("department") String department, Pageable pageable) {
        try {
            return employeeProcessingService.generateReport(department, pageable);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/employees/aveAge")
    public ResponseEntity<?> getAveAge() {
        return ResponseEntity.ok(employeeProcessingService.ageAverage());
    }

    @GetMapping("/employees/aveSalary")
    public ResponseEntity<?> getAveSalary() {
        return ResponseEntity.ok(employeeProcessingService.salaryAverage());
    }

    @PostMapping("/employee/add")
    public ResponseEntity<?> addEmployee(@RequestBody NewEmployeeDto newEmployeeDto) {

        Employee newEmployee = new Employee();
        newEmployee.setEmployeeId(newEmployeeDto.employeeId());
        newEmployee.setName(newEmployeeDto.name());
        newEmployee.setDateOfBirth(newEmployeeDto.dateOfBirth());

        if (newEmployeeDto.salary() == null) {
            newEmployee.setSalaryAmount(0d);
        }
        else {
            newEmployee.setSalaryAmount(newEmployeeDto.salary());
        }

        Department thisDepartment = new Department();
        if (newEmployeeDto.departmentName().isBlank()) {
            newEmployee.setDepartment(null);
        }
        else {
            thisDepartment.setDepartmentName(newEmployeeDto.departmentName());
            newEmployee.setDepartment(thisDepartment);
        }

        return employeeProcessingService.cdOps("add", newEmployee);
    }

    @PutMapping("/employee/update")
    public ResponseEntity<?> updateEmployee(@RequestParam("employeeId") String employeeId, @RequestBody UpdateEmployeeDto updateEmployeeDto) {

        Optional<Employee> employee = employeeRepository.findEmployeeByEmployeeId(employeeId);

        // This is never really called since only a single session can exist at a time
        // And removal is impossible to do while updating, unless a DB admin decides to mess around
        if (employee.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Employee not found!/nOr resigned...maybe"));

        return employeeProcessingService.updateOps(employee.get(), updateEmployeeDto);
    }

    @DeleteMapping("/employee/delete")
    public ResponseEntity<?> deleteEmployee(@RequestParam("employeeId") String employeeId) {

        Optional<Employee> employee = employeeRepository.findEmployeeByEmployeeId(employeeId);

        // This is never really called since only a single session can exist at a time
        // And removal is impossible to do while updating, unless a DB admin decides to mess around
        if (employee.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","Employee not found!/nOr resigned...maybe"));

        return employeeProcessingService.cdOps("delete", employee.get());
    }


}
