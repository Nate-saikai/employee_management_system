package com.capstone.employeemanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record EmployeeResponseDto(
        @JsonProperty("employeeId")
        String id,
        String name,
        LocalDate dateOfBirth,
        Double salaryAmount,

        @JsonProperty("departmentName")
        String department
) {
}
