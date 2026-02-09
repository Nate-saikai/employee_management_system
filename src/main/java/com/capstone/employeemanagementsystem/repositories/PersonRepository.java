package com.capstone.employeemanagementsystem.repositories;

import com.capstone.employeemanagementsystem.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findPersonByName(String name);
    Optional<Person> findPersonByEmployeeId(String employeeId);
}
