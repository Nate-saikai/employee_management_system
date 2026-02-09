package com.capstone.employeemanagementsystem.services;

import com.capstone.employeemanagementsystem.dto.UpdateEmployeeDto;
import com.capstone.employeemanagementsystem.models.Department;
import com.capstone.employeemanagementsystem.models.Employee;
import com.capstone.employeemanagementsystem.repositories.DepartmentRepository;
import com.capstone.employeemanagementsystem.repositories.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeProcessingServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeProcessingService service;

    // --- CRUD Tests ---
    @Test
    void addEmployee_NewEmployee_Success() {
        Employee emp = new Employee();
        emp.setEmployeeId("E123");
        Department dept = new Department();
        dept.setDepartmentName("HR");
        emp.setDepartment(dept);

        when(employeeRepository.findEmployeeByEmployeeId("E123")).thenReturn(Optional.empty());
        when(departmentRepository.findDepartmentByDepartmentNameIgnoreCase("HR")).thenReturn(Optional.of(dept));
        when(employeeRepository.save(emp)).thenReturn(emp);

        Employee result = service.addEmployee(emp);

        assertEquals("E123", result.getEmployeeId());
        assertEquals("HR", result.getDepartment().getDepartmentName());
        verify(employeeRepository).save(emp);
    }

    @Test
    void addEmployee_AlreadyExists_ThrowsException() {
        Employee emp = new Employee();
        emp.setEmployeeId("E123");

        when(employeeRepository.findEmployeeByEmployeeId("E123")).thenReturn(Optional.of(emp));

        assertThrows(IllegalArgumentException.class, () -> service.addEmployee(emp));
    }

    @Test
    void deleteEmployee_EmployeeExists_Success() {
        Employee emp = new Employee();
        emp.setPersonId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));

        Employee result = service.deleteEmployee(emp);

        assertEquals(emp, result);
        verify(employeeRepository).delete(emp);
    }

    @Test
    void deleteEmployee_NotFound_ThrowsException() {
        Employee emp = new Employee();
        emp.setPersonId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.deleteEmployee(emp));
    }

    // --- Averaging Tests ---
    @Test
    void ageAverage_WithEmployees_ReturnsCorrectAverage() {
        Employee emp1 = new Employee();
        emp1.setDateOfBirth(LocalDate.now().minusYears(30));
        Employee emp2 = new Employee();
        emp2.setDateOfBirth(LocalDate.now().minusYears(40));

        when(employeeRepository.findAll()).thenReturn(List.of(emp1, emp2));

        Double avg = service.ageAverage();

        assertTrue(avg >= 34.9 && avg <= 35.1); // approx 35
    }

    @Test
    void ageAverage_NoEmployees_ReturnsZero() {
        when(employeeRepository.findAll()).thenReturn(List.of());

        Double avg = service.ageAverage();

        assertEquals(0d, avg);
    }

    @Test
    void salaryAverage_WithEmployees_ReturnsCorrectAverage() {
        Employee emp1 = new Employee();
        emp1.setSalaryAmount(1000d);
        Employee emp2 = new Employee();
        emp2.setSalaryAmount(2000d);

        when(employeeRepository.findAll()).thenReturn(List.of(emp1, emp2));

        Double avg = service.salaryAverage();

        assertEquals(1500d, avg);
    }

    @Test
    void salaryAverage_NoEmployees_ReturnsZero() {
        when(employeeRepository.findAll()).thenReturn(List.of());

        Double avg = service.salaryAverage();

        assertEquals(0d, avg);
    }

    // --- Report Tests ---
    @Test
    void generateReport_AllEmployees_ReturnsPage() {
        Page<Employee> page = new PageImpl<>(List.of(new Employee()));
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(page);

        ResponseEntity<?> response = service.generateReport(1, Pageable.unpaged(), null);

        assertTrue(response.getBody() instanceof Page);
    }

    @Test
    void generateReport_ByDepartment_NotFound_ThrowsException() {
        when(departmentRepository.findDepartmentByDepartmentNameIgnoreCase("HR"))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> service.generateReport("HR", Pageable.unpaged()));
    }
}
