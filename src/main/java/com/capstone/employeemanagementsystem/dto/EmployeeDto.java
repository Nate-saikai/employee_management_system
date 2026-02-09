package com.capstone.employeemanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record EmployeeDto(

        @JsonProperty("employeeId")
        String id,
        String name,
        LocalDate dateOfBirth,

        @JsonProperty("passwordHash")
        String password,
        Double salary,

        @JsonProperty("departmentName")
        String department
) {
}
