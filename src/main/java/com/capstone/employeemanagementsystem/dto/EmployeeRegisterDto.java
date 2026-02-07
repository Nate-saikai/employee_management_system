package com.capstone.employeemanagementsystem.dto;

import java.time.LocalDate;

public record EmployeeRegisterDto(
        Long id,
        String name,
        LocalDate dateOfBirth,
        String password,
        Double salary,
        String department
) {
}
