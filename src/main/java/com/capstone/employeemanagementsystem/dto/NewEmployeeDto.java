package com.capstone.employeemanagementsystem.dto;

import java.time.LocalDate;

public record NewEmployeeDto(
        String employeeId,
        String name,
        LocalDate dateOfBirth,
        Double salary,
        String departmentName
) {
}
