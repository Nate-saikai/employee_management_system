package com.capstone.employeemanagementsystem.services;

import com.capstone.employeemanagementsystem.dto.DepartmentDto;
import com.capstone.employeemanagementsystem.exception.DepartmentNotFoundException;
import com.capstone.employeemanagementsystem.exception.DuplicateDepartmentException;
import com.capstone.employeemanagementsystem.models.Department;
import com.capstone.employeemanagementsystem.models.Employee;
import com.capstone.employeemanagementsystem.repositories.DepartmentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.sql.model.jdbc.DeleteOrUpsertOperation;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

@AllArgsConstructor
@Service
public class DepartmentProcessingService implements AbstractProcessingService {

    private final DepartmentRepository departmentRepository;

    public ResponseEntity<?> getAllDepartments(Pageable pageable) {
        return ResponseEntity.ok(departmentRepository.findAll(pageable));
    }

    @Override
    public ResponseEntity<?> search(String search, Pageable pageable) {
        Page<Department> departments = departmentRepository.searchAllByDepartmentNameContainsIgnoreCase(search, pageable);

        if (departments.isEmpty()) throw new DepartmentNotFoundException("No matches Found");

        return ResponseEntity.ok(departments);
    }

    @Override
    @Transactional
    public ResponseEntity<?> cdOps(String select, String departmentName, Long departmentId) throws SQLIntegrityConstraintViolationException {
        return switch (select) {
            case "add" -> ResponseEntity.ok(addDepartment(departmentName));
            case "delete" -> ResponseEntity.ok(deleteDepartment(departmentId));
            default -> throw new IllegalArgumentException("Choice is invalid!");
        };
    }


    public Department addDepartment(String departmentName) {

        Optional<Department> deptExists = departmentRepository.findDepartmentByDepartmentNameIgnoreCase(departmentName);

        if (deptExists.isPresent()) throw new DuplicateDepartmentException("Department already exists");

        Department newDepartment = new Department();
        newDepartment.setDepartmentName(departmentName);
        newDepartment.setPersonList(new ArrayList<>());

        return departmentRepository.save(newDepartment);
    }

    public DepartmentDto deleteDepartment(Long departmentId) throws SQLIntegrityConstraintViolationException {

        Optional<Department> deptExists = departmentRepository.findById(departmentId);

        if (deptExists.isEmpty()) throw new DepartmentNotFoundException("Not Found");

        try {
            departmentRepository.delete(deptExists.get());
        } catch (DataIntegrityViolationException e) {
            throw new SQLIntegrityConstraintViolationException(e);
        }

        return deptExists
                .map(dept -> new DepartmentDto(
                        dept.getDepartmentName()
                )).get();
    }

    @Transactional
    public void updateDepartment(DepartmentDto departmentDto, Long departmentId) {

        Optional<Department> deptExists = departmentRepository.findDepartmentByDepartmentNameIgnoreCase(departmentDto.departmentName());

        if (deptExists.isPresent()) throw new DuplicateDepartmentException("Department already exists");

        Optional<Department> thisDept = departmentRepository.findById(departmentId);

        thisDept.ifPresent(department -> department.setDepartmentName(departmentDto.departmentName()));

    }

}
