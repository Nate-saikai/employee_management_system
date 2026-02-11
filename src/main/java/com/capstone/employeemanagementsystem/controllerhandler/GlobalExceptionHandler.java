package com.capstone.employeemanagementsystem.controllerhandler;

import com.capstone.employeemanagementsystem.exception.DepartmentNotFoundException;
import com.capstone.employeemanagementsystem.exception.DuplicateDepartmentException;
import com.capstone.employeemanagementsystem.exception.DuplicateEmployeeException;
import com.capstone.employeemanagementsystem.exception.EmployeeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex, Locale locale) {
        Map<String, String> error = new HashMap<>();
        error.put("error", messageSource.getMessage("illegal.argument", null, locale));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.security.core.userdetails.UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFound(org.springframework.security.core.userdetails.UsernameNotFoundException ex, Locale locale) {
        Map<String, String> error = new HashMap<>();
        error.put("error", messageSource.getMessage("username.notfound", null, locale));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(java.util.NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNoSuchElement(NoSuchElementException ex, Locale locale) {
        Map<String, String> error = new HashMap<>();
        error.put("error", messageSource.getMessage("no.such.element", null, locale));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex, Locale locale) {
        Map<String, String> error = new HashMap<>();
        String err = "";
        if (ex.getMessage().contains("duplicate")) {
            err = messageSource.getMessage("data.integrity.error", null, locale);
        }
        else if (ex.getMessage().contains("foreign")) {
            err = messageSource.getMessage("sql.integrity.error", null, locale);
        }
        error.put("error", err);
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<?> handleDepartmentNotFound(DepartmentNotFoundException ex, Locale locale) {
        Map<String, String> error = new HashMap<>();
        error.put("error", messageSource.getMessage("department.nosuchelement", null, locale));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<?> handleEmployeeNotFound(EmployeeNotFoundException ex, Locale locale) {
        Map<String, String> error = new HashMap<>();
        error.put("error", messageSource.getMessage("employee.nosuchelement", null, locale));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateDepartmentException.class)
    public ResponseEntity<?> handleDuplicateDepartment(DuplicateDepartmentException ex, Locale locale) {
        Map<String, String> error = new HashMap<>();
        error.put("error", messageSource.getMessage("duplicate.department", null, locale));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateEmployeeException.class)
    public ResponseEntity<?> handleDuplicateEmployee(DuplicateEmployeeException ex, Locale locale) {
        Map<String, String> error = new HashMap<>();
        error.put("error", messageSource.getMessage("duplicate.employee", null, locale));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
