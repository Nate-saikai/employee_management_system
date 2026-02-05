package com.capstone.employeemanagementsystem.services;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
class MathCalculationsService {

    public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if (birthDate == null || currentDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        return Period.between(birthDate, currentDate).getYears();
    }

}
