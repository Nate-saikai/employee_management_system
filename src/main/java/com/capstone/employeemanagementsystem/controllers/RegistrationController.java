package com.capstone.employeemanagementsystem.controllers;

import com.capstone.employeemanagementsystem.dto.EmployeeDto;
import com.capstone.employeemanagementsystem.services.UserRegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
public class RegistrationController {

    private final UserRegisterService userRegisterService;

    public RegistrationController(UserRegisterService userRegisterService) {
        this.userRegisterService = userRegisterService;
    }

    @PostMapping("/user/register")
    public ResponseEntity<?> registerNewManager(@RequestBody EmployeeDto manager) {

        try {
            userRegisterService.registerUser(manager);

            return ResponseEntity.ok("Manager added!");
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
        catch (NoSuchElementException n) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", n.getMessage()));
        }
    }
}
