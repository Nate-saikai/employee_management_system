package com.capstone.employeemanagementsystem.services;

import com.capstone.employeemanagementsystem.repositories.PersonRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    public CustomUserDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String personId) throws UsernameNotFoundException {
        System.out.println("Attempting login for person id: " + personId);

        Long id;
        try {
            id = Long.parseLong(personId);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid person id format: " + personId);
        }

        return personRepository.findById(id)
                .map(person -> {
                    System.out.println("Found user: " + person.getName());
                    return org.springframework.security.core.userdetails.User
                            .withUsername(String.valueOf(person.getPersonId())) // use id as username
                            .password(person.getPasswordHash()) // hashed password from DB
                            .roles("USER") // assign roles as needed
                            .build();
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + personId));
    }


}
