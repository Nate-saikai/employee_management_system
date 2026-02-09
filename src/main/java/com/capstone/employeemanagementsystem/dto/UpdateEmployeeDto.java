package com.capstone.employeemanagementsystem.dto;

import java.time.LocalDate;

public record UpdateEmployeeDto(
        String name,
        LocalDate dateOfBirth,
        Double salary,
        String departmentName
) {
}
