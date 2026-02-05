package com.capstone.employeemanagementsystem.services;

import com.capstone.employeemanagementsystem.models.Department;
import com.capstone.employeemanagementsystem.repositories.DepartmentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.sql.model.jdbc.DeleteOrUpsertOperation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

@AllArgsConstructor
@Service
public class DepartmentProcessingService {

    private final DepartmentRepository departmentRepository;

    @Transactional
    public Department addDepartment(String departmentName) {

        Optional<Department> deptExists = departmentRepository.findDepartmentByDepartmentNameContainsIgnoreCase(departmentName);

        if (deptExists.isPresent()) throw new IllegalArgumentException("Department already exists");

        Department newDepartment = new Department();
        newDepartment.setDepartmentName(departmentName);
        newDepartment.setPersonList(new ArrayList<>());

        return departmentRepository.save(newDepartment);
    }

}
