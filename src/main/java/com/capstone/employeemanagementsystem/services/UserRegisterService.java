package com.capstone.employeemanagementsystem.services;

import com.capstone.employeemanagementsystem.dto.EmployeeDto;
import com.capstone.employeemanagementsystem.exception.DepartmentNotFoundException;
import com.capstone.employeemanagementsystem.models.Manager;
import com.capstone.employeemanagementsystem.repositories.DepartmentRepository;
import com.capstone.employeemanagementsystem.repositories.ManagerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@AllArgsConstructor
@Service
public class UserRegisterService {

    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public void registerUser (EmployeeDto manager) {

        if (managerRepository.findManagerByEmployeeId(manager.id()).isPresent()) {
            throw new IllegalArgumentException("User already exists!");
        }

        Manager newManager = new Manager();
        newManager.setEmployeeId(manager.id());
        newManager.setName(manager.name());
        newManager.setDateOfBirth(manager.dateOfBirth());

        if (manager.salary() == null || manager.salary().isNaN()) {
            newManager.setSalaryAmount(0.00);
        }
        else {
            newManager.setSalaryAmount(manager.salary());
        }

        newManager.setPasswordHash(passwordEncoder.encode(manager.password()));

        if (manager.department().isBlank()) {
            newManager.setDepartment(null);
        }
        else {
            newManager.setDepartment(departmentRepository.findDepartmentByDepartmentNameIgnoreCase(manager.department())
                    .orElseThrow(() -> new DepartmentNotFoundException("Department not Found")));
        }

        /* works, child employee updated */
        managerRepository.save(newManager);



    }

}
