package com.capstone.employeemanagementsystem.controllers;

import com.capstone.employeemanagementsystem.dto.DepartmentDto;
import com.capstone.employeemanagementsystem.services.DepartmentProcessingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

@RestController
@RequestMapping("${api.department.base}")
public class DepartmentManagementController {

    private final DepartmentProcessingService departmentProcessingService;

    public DepartmentManagementController(DepartmentProcessingService departmentProcessingService) {
        this.departmentProcessingService = departmentProcessingService;
    }

    // ------------------------- READ ----------------------------

    @GetMapping("/all")
    public ResponseEntity<?> getAllDepartments(Pageable pageable) {
        return departmentProcessingService.getAllDepartments(pageable);
    }

    /**
     *
     * @param pageable {@link Pageable}
     * @return A {@link Page} of matching Departments by substring
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchDepartment(@RequestParam("name") String search, Pageable pageable) {
        return departmentProcessingService.search(search, pageable);
    }

    // -----------------------------------------------------------
    // -------------------------- CUD ----------------------------
    // -----------------------------------------------------------

    @PostMapping("/add")
    public ResponseEntity<?> addDepartment(@RequestBody DepartmentDto departmentDto) throws SQLIntegrityConstraintViolationException {
        System.out.println(departmentDto.departmentName());
        return ResponseEntity.ok(departmentProcessingService.cdOps("add", departmentDto.departmentName(), null));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateDepartment(@RequestBody DepartmentDto departmentDto, @RequestParam Long departmentId) {
        departmentProcessingService.updateDepartment(departmentDto, departmentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteDepartment(@RequestParam Long departmentId) throws SQLIntegrityConstraintViolationException {
        return ResponseEntity.ok(departmentProcessingService.cdOps("delete", null, departmentId));
    }


}
