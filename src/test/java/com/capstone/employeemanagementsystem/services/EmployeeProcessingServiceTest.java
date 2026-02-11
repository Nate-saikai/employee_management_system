package com.capstone.employeemanagementsystem.services;

import com.capstone.employeemanagementsystem.dto.NewEmployeeDto;
import com.capstone.employeemanagementsystem.dto.UpdateEmployeeDto;
import com.capstone.employeemanagementsystem.exception.DepartmentNotFoundException;
import com.capstone.employeemanagementsystem.exception.EmployeeNotFoundException;
import com.capstone.employeemanagementsystem.models.Department;
import com.capstone.employeemanagementsystem.models.Employee;
import com.capstone.employeemanagementsystem.repositories.DepartmentRepository;
import com.capstone.employeemanagementsystem.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeProcessingServiceTest {

    private DepartmentRepository departmentRepository;
    private EmployeeRepository employeeRepository;
    private EmployeeProcessingService service;

    @BeforeEach
    void setUp() {
        departmentRepository = mock(DepartmentRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        service = new EmployeeProcessingService(departmentRepository, employeeRepository);
    }

    @Test
    void testGenerateReport_AllEmployees() {
        Employee emp = new Employee();
        emp.setEmployeeId("E001");
        emp.setName("John Doe");
        emp.setDateOfBirth(LocalDate.of(1990,1,1));
        emp.setSalaryAmount(5000d);

        Page<Employee> page = new PageImpl<>(List.of(emp));
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(page);

        ResponseEntity<?> response = service.generateReport(1, Pageable.unpaged(), null);

        assertTrue(response.getBody() instanceof Page<?>);
        Page<?> result = (Page<?>) response.getBody();
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGenerateReport_EmployeeById() {
        Employee emp = new Employee();
        emp.setEmployeeId("E002");
        emp.setName("Jane Doe");
        emp.setDateOfBirth(LocalDate.of(1985,5,5));
        emp.setSalaryAmount(6000d);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));

        ResponseEntity<?> response = service.generateReport(2, Pageable.unpaged(), 1L);

        assertTrue(response.getBody() instanceof Employee);
        Employee result = (Employee) response.getBody();
        assertEquals("Jane Doe", result.getName());
    }

    @Test
    void testGenerateReport_DepartmentNotFound() {
        when(departmentRepository.findAllByDepartmentNameContainsIgnoreCase("HR"))
                .thenReturn(List.of());

        assertThrows(DepartmentNotFoundException.class,
                () -> service.generateReport("HR", Pageable.unpaged()));
    }

    @Test
    void testAddEmployee_DepartmentExists() {
        NewEmployeeDto dto = new NewEmployeeDto("E003", "Alice", LocalDate.of(1992,2,2), 4000d, "IT");
        Department dept = new Department();
        dept.setDepartmentName("IT");

        when(departmentRepository.findDepartmentByDepartmentNameIgnoreCase("IT"))
                .thenReturn(Optional.of(dept));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        Employee saved = service.addEmployee(dto);

        assertEquals("Alice", saved.getName());
        assertEquals("IT", saved.getDepartment().getDepartmentName());
    }

    @Test
    void testDeleteEmployee_Success() {
        Employee emp = new Employee();
        emp.setEmployeeId("E004");
        emp.setName("Bob");
        emp.setDateOfBirth(LocalDate.of(1991,3,3));
        emp.setSalaryAmount(3000d);

        when(employeeRepository.findEmployeeByEmployeeId("E004")).thenReturn(Optional.of(emp));

        Employee deleted = service.deleteEmployee(new NewEmployeeDto("E004", "Bob", LocalDate.now(), 3000d, ""));

        verify(employeeRepository).delete(emp);
        assertEquals("Bob", deleted.getName());
    }

    @Test
    void testDeleteEmployee_NotFound() {
        when(employeeRepository.findEmployeeByEmployeeId("Ghost")).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class,
                () -> service.deleteEmployee(new NewEmployeeDto("Ghost", "Ghost", LocalDate.now(), 0d, "")));
    }

    @Test
    void testUpdateEmployee_DepartmentNotFound() {
        Employee emp = new Employee();
        emp.setEmployeeId("E005");
        emp.setName("Carl");
        emp.setDateOfBirth(LocalDate.of(1990,1,1));
        emp.setSalaryAmount(2000d);

        UpdateEmployeeDto dto = new UpdateEmployeeDto(null, null, null, "UnknownDept");

        when(departmentRepository.findDepartmentByDepartmentNameIgnoreCase("UnknownDept"))
                .thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class,
                () -> service.updateEmployee(emp, dto));
    }

    @Test
    void testAgeAverage() {
        Employee e1 = new Employee();
        e1.setEmployeeId("E006");
        e1.setName("A");
        e1.setDateOfBirth(LocalDate.of(2000,1,1));
        e1.setSalaryAmount(1000d);

        Employee e2 = new Employee();
        e2.setEmployeeId("E007");
        e2.setName("B");
        e2.setDateOfBirth(LocalDate.of(1990,1,1));
        e2.setSalaryAmount(2000d);

        when(employeeRepository.findAll()).thenReturn(List.of(e1, e2));

        Double avg = service.ageAverage();
        assertTrue(avg > 0);
    }

    @Test
    void testSalaryAverage() {
        Employee e1 = new Employee();
        e1.setEmployeeId("E008");
        e1.setName("A");
        e1.setDateOfBirth(LocalDate.of(2000,1,1));
        e1.setSalaryAmount(1000d);

        Employee e2 = new Employee();
        e2.setEmployeeId("E009");
        e2.setName("B");
        e2.setDateOfBirth(LocalDate.of(1990,1,1));
        e2.setSalaryAmount(2000d);

        when(employeeRepository.findAll()).thenReturn(List.of(e1, e2));

        Double avg = service.salaryAverage();
        assertEquals(1500d, avg);
    }
}
