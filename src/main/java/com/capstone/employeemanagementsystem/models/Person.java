package com.capstone.employeemanagementsystem.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personId;

    @Column(nullable = false, unique = true)
    @NotBlank
    private Long employeeId;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false)
    @NotBlank
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    @NotBlank
    private String passwordHash;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "departmentId")
    @JsonBackReference
    private Department department;

    @Column
    @NotBlank
    private Double salaryAmount;
}
