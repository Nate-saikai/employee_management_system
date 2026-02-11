package com.capstone.employeemanagementsystem.services;

import com.capstone.employeemanagementsystem.dto.EmployeeResponseDto;
import com.capstone.employeemanagementsystem.dto.NewEmployeeDto;
import com.capstone.employeemanagementsystem.dto.UpdateEmployeeDto;
import com.capstone.employeemanagementsystem.exception.DepartmentNotFoundException;
import com.capstone.employeemanagementsystem.exception.EmployeeNotFoundException;
import com.capstone.employeemanagementsystem.models.Department;
import com.capstone.employeemanagementsystem.models.Employee;
import com.capstone.employeemanagementsystem.models.Person;
import com.capstone.employeemanagementsystem.repositories.DepartmentRepository;
import com.capstone.employeemanagementsystem.repositories.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.capstone.employeemanagementsystem.services.MathCalculationsService.calculateAge;

@AllArgsConstructor
@Service
public class EmployeeProcessingService implements PersonProcessingService {

    private final DepartmentRepository departmentRepository;

    EmployeeRepository employeeRepository;

    /**
     *
     * @param select {@code int}
     * @param pageable {@link Pageable}
     * @param id {@link Long}
     * @return value depends on the selected operation
     * <br><br>
     * This operation consolidates {@code getAllEmployeeDetails}
     * and {@code getEmployeeDetails} services
     * <br>
     * DEV NOTE: This is a little funky, relying on hardcoded integers
     */
    @Override
    public ResponseEntity<?> generateReport(int select, Pageable pageable, Long id) {

        return switch (select) {
            case 1 -> ResponseEntity.ok(getAllEmployeeDetails(pageable));
            case 2 -> ResponseEntity.ok(getEmployeeDetails(id));
            default -> ResponseEntity.ok("redirect:/home"); // As far as the code goes, this won't get tripped
        };
    }

    /**
     *
     * @param department {@link Department} filter
     * @param pageable {@link Pageable}
     * @return {@link ResponseEntity} JSON payload containing Employees by Department
     */
    @Override
    public ResponseEntity<?> generateReport(String department, Pageable pageable) {

        List<Department> deptExists = departmentRepository.findAllByDepartmentNameContainsIgnoreCase(department);

        if (deptExists.isEmpty()) throw new DepartmentNotFoundException("No departments match: " + department);

        Page<EmployeeResponseDto> allEmpByDept = employeeRepository.findAllByDepartmentIn(deptExists, pageable)
                .map(emp -> new EmployeeResponseDto(
                    emp.getEmployeeId(),
                    emp.getName(),
                    emp.getDateOfBirth(),
                    emp.getSalaryAmount(),
                    emp.getDepartment() == null ? null : emp.getDepartment().getDepartmentName()
                ));

        return ResponseEntity.ok(allEmpByDept);
    }

    /**
     *
     * @param age {@link Integer} filter
     * @param pageable {@link Pageable}
     * @return {@link ResponseEntity} JSON payload containing Employees by Exact Age
     */
    @Override
    public ResponseEntity<?> generateReport(Integer age, Pageable pageable) {

        /* ----------------------- NATIVE SQL IS WORKING -------------------------- */
        Page<EmployeeResponseDto> empAge = employeeRepository.findEmployeesByExactAge(age, pageable)
                .map(emp -> new EmployeeResponseDto(
                        emp.getEmployeeId(),
                        emp.getName(),
                        emp.getDateOfBirth(),
                        emp.getSalaryAmount(),
                        emp.getDepartment() == null ? null : emp.getDepartment().getDepartmentName()
                ));

        return ResponseEntity.ok(empAge);
    }


    /**
     *
     * @param select {@link String} "add" or "delete", case-sensitive
     * @return value depends on the selected operation
     * <br><br>
     * This operation consolidates CD operations like
     * {@code addEmployee}, {@code deleteEmployee}
     * <br>
     * DEV NOTE: This is a little funky, relying on hardcoded strings
     */
    @Override
    @Transactional
    public ResponseEntity<?> cdOps(String select, NewEmployeeDto employeeDto) {
        return switch (select) {
            case "add" -> ResponseEntity.ok(addEmployee(employeeDto));
            case "delete" -> ResponseEntity.ok(deleteEmployee(employeeDto));
            default -> throw new IllegalArgumentException("Choice is invalid!");
        };
    }

    /**
     *
     * @param employee {@link Employee} to be updated
     * @param updateEmployeeDto {@link UpdateEmployeeDto} JSON request payload for update fields
     * @return {@link ResponseEntity} containing status (updated, not updated)
     */
    @Transactional
    public ResponseEntity<?> updateOps(Employee employee, UpdateEmployeeDto updateEmployeeDto) {
        return updateEmployee(employee, updateEmployeeDto);
    }

    /**
     *
     * @param pageable {@link Pageable}
     * @return {@link Page} of all employees and details
     */
    @Transactional
    private Page<EmployeeResponseDto> getAllEmployeeDetails(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(emp -> new EmployeeResponseDto(
                emp.getEmployeeId(),
                emp.getName(),
                emp.getDateOfBirth(),
                emp.getSalaryAmount(),
                emp.getDepartment() == null ? null : emp.getDepartment().getDepartmentName()
        ));
    }

    /**
     *
     * @param id {@link Long}
     * @return {@link Employee} details
     */
    private Employee getEmployeeDetails(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee does not exist!"));
    }

    /* ------------------------------------------------------------------------------------- */
    /* ------------------------------------ CRUD OPERATIONS -------------------------------- */
    /* ------------------------------------------------------------------------------------- */

    protected Employee addEmployee(NewEmployeeDto newEmployeeDto) {

        Employee addEmployee = new Employee();
        addEmployee.setEmployeeId(newEmployeeDto.employeeId());
        addEmployee.setName(newEmployeeDto.name());
        addEmployee.setDateOfBirth(newEmployeeDto.dateOfBirth());

        if (newEmployeeDto.salary() == null) {
            addEmployee.setSalaryAmount(0d);
        }
        else {
            addEmployee.setSalaryAmount(newEmployeeDto.salary());
        }

        Department thisDepartment = new Department();
        if (newEmployeeDto.departmentName().isBlank()) {
            addEmployee.setDepartment(null);
        }
        else {
            thisDepartment.setDepartmentName(newEmployeeDto.departmentName());
            addEmployee.setDepartment(thisDepartment);
        }

//        Optional<Employee> empExists = employeeRepository.findEmployeeByEmployeeId(addEmployee.getEmployeeId());
//
//        if (empExists.isPresent()) throw new IllegalArgumentException("Employee already employed");

        if (addEmployee.getDepartment() != null) {
            Optional<Department> deptExists = departmentRepository.findDepartmentByDepartmentNameIgnoreCase(addEmployee.getDepartment().getDepartmentName());

            if (deptExists.isPresent()) {
                addEmployee.setDepartment(deptExists.get());
            }
            else {
                addEmployee.setDepartment(null);
            }
        }

        return employeeRepository.save(addEmployee);
    }

    protected Employee deleteEmployee(NewEmployeeDto employeeDto) {

        Employee deletedEmployee = employeeRepository.findEmployeeByEmployeeId(employeeDto.employeeId()).
                orElseThrow(() -> new EmployeeNotFoundException("Employee does not exist!"));

        employeeRepository.delete(deletedEmployee);

        return deletedEmployee;
    }

    protected ResponseEntity<?> updateEmployee(
            Employee employee,
            UpdateEmployeeDto updateEmployeeDto) {

        boolean updated = false;

        if (updateEmployeeDto.name() != null && !updateEmployeeDto.name().isBlank()) {
            employee.setName(updateEmployeeDto.name());
            updated = true;
        }
        if (updateEmployeeDto.dateOfBirth() != null) {
            employee.setDateOfBirth(updateEmployeeDto.dateOfBirth());
            updated = true;
        }
        if(updateEmployeeDto.salary() != null) {
            employee.setSalaryAmount(updateEmployeeDto.salary());
            updated = true;
        }
        if (updateEmployeeDto.departmentName() != null && !updateEmployeeDto.departmentName().isBlank()) {

            // -------------------------------------------------------------------------------------
            // Check if the department exists
            // Maybe the user is looking in the wrong company, hehe~
            // -------------------------------------------------------------------------------------

            Optional<Department> deptExists = departmentRepository
                    .findDepartmentByDepartmentNameIgnoreCase(updateEmployeeDto.departmentName());

            if (deptExists.isEmpty()) {
                throw new DepartmentNotFoundException("This department does not exist.");
            }

            // -------------------------------------------------------------------------------------
            // Now check if the employee is already a member of the department
            // Can't let them double down, yeah?
            // -------------------------------------------------------------------------------------


            Optional<Employee> member = employeeRepository
                    .findEmployeeByDepartmentAndNameIgnoreCase(deptExists.get(), employee.getName());

            if (member.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Employee already belongs to department " + updateEmployeeDto.departmentName()));
            }

            // -------------------------------------------------------------------------------------
            // Passed all tests?
            // Bring in the new recruit
            // -------------------------------------------------------------------------------------

            employee.setDepartment(deptExists.get());
            deptExists.get().getPersonList().add(employee);
            updated = true;
        }

        if (!updated) {
            return ResponseEntity.badRequest().body(Map.of("error", "No Updates Made."));
        }

        return ResponseEntity.ok(Map.of("success", "Updates made for: " + employee.getName()));
    }

    /* ------------------------------------------------------------------------------------- */
    /* --------------------------------- AVERAGING OPERATIONS ------------------------------ */
    /* ------------------------------------------------------------------------------------- */

    /**
     *
     * @return {@link Double} average age of all employees
     */
    public Double ageAverage() {

        List<Employee> allEmployees = employeeRepository.findAll();

        if (allEmployees.isEmpty()) {
            return 0d;
        }

        return allEmployees.stream()
                .mapToDouble(emp -> calculateAge(
                        emp.getDateOfBirth(),
                        LocalDate.now()))
                .sum() / allEmployees.size();
    }

    /**
     *
     * @return {@link Double} average salary of all employees
     */
    public Double salaryAverage() {

        List<Employee> allEmployees = employeeRepository.findAll();

        if (allEmployees.isEmpty()) {
            return 0d;
        }

        double salSum = allEmployees.stream()
                .mapToDouble(Person::getSalaryAmount)
                .sum();

        if (salSum < 1) {
            return 0d;
        }
        else {
            return salSum / allEmployees.size();
        }


    }
}
