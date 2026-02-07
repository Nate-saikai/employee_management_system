package com.capstone.employeemanagementsystem.services;

import com.capstone.employeemanagementsystem.dto.EmployeeRegisterDto;
import com.capstone.employeemanagementsystem.models.Employee;
import com.capstone.employeemanagementsystem.models.Person;
import com.capstone.employeemanagementsystem.repositories.DepartmentRepository;
import com.capstone.employeemanagementsystem.repositories.EmployeeRepository;
import com.capstone.employeemanagementsystem.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@AllArgsConstructor
@Service
public class UserRegisterService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public void registerUser (EmployeeRegisterDto employee) {

        if (employeeRepository.findEmployeeByEmployeeId(employee.id())) {
            throw new IllegalArgumentException("Employee already exists!");
        }

        Person newEmployee = new Employee();
        newEmployee.setEmployeeId(employee.id());
        newEmployee.setName(employee.name());
        newEmployee.setDateOfBirth(employee.dateOfBirth());
        newEmployee.setSalaryAmount(employee.salary());
        newEmployee.setPasswordHash(passwordEncoder.encode(employee.password()));

        // TODO: Is volatile, will see whether payload returns empty string
        newEmployee.setDepartment(departmentRepository.findDepartmentByDepartmentNameIgnoreCase(employee.department())
                .orElseThrow(() -> new NoSuchElementException("Department not Found")));

        // TODO: Check database after use, might not update child employee
        personRepository.save(newEmployee);



    }

}
