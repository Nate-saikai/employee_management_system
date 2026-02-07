package com.capstone.employeemanagementsystem.repositories;

import com.capstone.employeemanagementsystem.models.Department;
import com.capstone.employeemanagementsystem.models.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findEmployeeByDepartmentAndNameIgnoreCase(Department department, String name);

    Page<Employee> findAllByDepartment(Department department, Pageable pageable);

    /**
     *
     * @param age {@code int} age parameter
     * @param pageable {@link Pageable}
     * @return Pageable list of all employees by exact age
     */
    @Query(
            value = """
        SELECT p.*
        FROM employee_table e
        JOIN person p ON e.person_id = p.person_id
        WHERE (YEAR(CURDATE()) - YEAR(p.date_of_birth))
              - (DATE_FORMAT(CURDATE(), '%m%d') < DATE_FORMAT(p.date_of_birth, '%m%d')) = :age
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM employee_table e
        JOIN person p ON e.person_id = p.person_id
        WHERE (YEAR(CURDATE()) - YEAR(p.date_of_birth))
              - (DATE_FORMAT(CURDATE(), '%m%d') < DATE_FORMAT(p.date_of_birth, '%m%d')) = :age
        """,
            nativeQuery = true
    )
    Page<Employee> findEmployeesByExactAge(@Param("age") int age, Pageable pageable);


    /**
     *
     * @param minAge {@code int} minimum age range
     * @param maxAge {@code int} maximum age range
     * @param pageable {@link Pageable}
     * @return Pageable list of all employees by age range
     */
    @Query(
            value = """
        SELECT p.*
        FROM employee_table e
        JOIN person p ON e.person_id = p.person_id
        WHERE p.date_of_birth BETWEEN
              DATE_SUB(CURDATE(), INTERVAL :maxAge YEAR)
          AND DATE_SUB(CURDATE(), INTERVAL :minAge YEAR)
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM employee_table e
        JOIN person p ON e.person_id = p.person_id
        WHERE p.date_of_birth BETWEEN
              DATE_SUB(CURDATE(), INTERVAL :maxAge YEAR)
          AND DATE_SUB(CURDATE(), INTERVAL :minAge YEAR)
        """,
            nativeQuery = true
    )
    Page<Employee> findEmployeesByAgeRange(@Param("minAge") int minAge, @Param("maxAge") int maxAge, Pageable pageable);




    boolean findEmployeeByEmployeeId(Long employeeId);
}
