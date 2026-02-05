package com.capstone.employeemanagementsystem.repositories;

import com.capstone.employeemanagementsystem.models.Department;
import com.capstone.employeemanagementsystem.models.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findEmployeeByDepartmentAndNameIgnoreCase(Department department, String name);

    List<Employee> findAllByDepartment(Department department, Pageable pageable);

    // Find employees with exact age equal to age
    @Query(
            value = "SELECT p.* FROM employee_table e " +
                    "JOIN person p ON e.person_id = p.person_id " +
                    "WHERE (YEAR(CURDATE()) - YEAR(p.date_of_birth)) " +
                    "      - (DATE_FORMAT(CURDATE(), '%m%d') < DATE_FORMAT(p.date_of_birth, '%m%d')) = :age",
            nativeQuery = true
    )
    List<Employee> findEmployeesByExactAge(@Param("age") int age);


}
