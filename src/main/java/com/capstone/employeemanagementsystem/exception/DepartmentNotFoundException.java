package com.capstone.employeemanagementsystem.exception;

public class DepartmentNotFoundException extends RuntimeException {
    public DepartmentNotFoundException (String message) {
        super(message);
    }
}
