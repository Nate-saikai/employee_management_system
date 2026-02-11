package com.capstone.employeemanagementsystem.controllers;

import com.capstone.employeemanagementsystem.dto.NewEmployeeDto;
import com.capstone.employeemanagementsystem.dto.UpdateEmployeeDto;
import com.capstone.employeemanagementsystem.models.Employee;
import com.capstone.employeemanagementsystem.repositories.EmployeeRepository;
import com.capstone.employeemanagementsystem.services.EmployeeProcessingService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("${api.employees.base}")
public class EmployeeManagementController {

    private final EmployeeRepository employeeRepository;
    private final EmployeeProcessingService employeeProcessingService;

    public EmployeeManagementController(EmployeeRepository employeeRepository, EmployeeProcessingService employeeProcessingService) {
        this.employeeRepository = employeeRepository;
        this.employeeProcessingService = employeeProcessingService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllEmployees(Pageable pageable) {

        return employeeProcessingService.generateReport(1, pageable, 0L);
        // id does nothing here
    }

    @GetMapping("/all/age")
    public ResponseEntity<?> getAllEmployeesByExactAge(@RequestParam("age") Integer age, Pageable pageable) {

        return employeeProcessingService.generateReport(age, pageable);
    }

    @GetMapping("/all/department")
    public ResponseEntity<?> getAllEmployeesByDepartment(@RequestParam("department") String department, Pageable pageable) {

        return employeeProcessingService.generateReport(department, pageable);

    }

    @GetMapping("/aveAge")
    public ResponseEntity<?> getAveAge() {
        return ResponseEntity.ok(employeeProcessingService.ageAverage());
    }

    @GetMapping("/aveSalary")
    public ResponseEntity<?> getAveSalary() {
        return ResponseEntity.ok(employeeProcessingService.salaryAverage());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEmployee(@RequestBody NewEmployeeDto newEmployeeDto) {

        return employeeProcessingService.cdOps("add", newEmployeeDto);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateEmployee(@RequestParam("employeeId") String employeeId, @RequestBody UpdateEmployeeDto updateEmployeeDto) {

        Optional<Employee> employee = employeeRepository.findEmployeeByEmployeeId(employeeId);

        // This is never really called since only a single session can exist at a time
        // And updating is tied to a button that is only present if employee exists
        if (employee.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Employee not found!/nOr resigned...maybe"));

        return employeeProcessingService.updateOps(employee.get(), updateEmployeeDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteEmployee(@RequestParam("employeeId") String employeeId) {

        Optional<Employee> employee = employeeRepository.findEmployeeByEmployeeId(employeeId);

        // This is never really called since only a single session can exist at a time
        // And removal is impossible to do while updating, unless a DB admin decides to mess around
        if (employee.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","Employee not found!/nOr resigned...maybe"));

        return employeeProcessingService.cdOps("delete", new NewEmployeeDto(
                employee.get().getEmployeeId(),
                employee.get().getName(),
                employee.get().getDateOfBirth(),
                employee.get().getSalaryAmount(),
                employee.get().getDepartment() == null ? null : employee.get().getDepartment().getDepartmentName()
        ));
    }


}
