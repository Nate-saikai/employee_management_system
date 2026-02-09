package com.capstone.employeemanagementsystem.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ManagerTable")
public class Manager extends Person {

    @NotBlank
    @Column(name = "passwordHash", nullable = false)
    private String passwordHash;

}
