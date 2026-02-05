package com.capstone.employeemanagementsystem.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "EmployeeTable")
public class Employee extends Person {
}
