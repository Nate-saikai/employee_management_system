package com.capstone.employeemanagementsystem.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {
    @GetMapping({"/", "/login", "/register", "/employees/**", "/departments/**"})
    public String forward() {
        return "index";
    }
}
