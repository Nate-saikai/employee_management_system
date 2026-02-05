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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Attempting login for: " + username);
        return personRepository.findPersonByName(username)
                .map(person -> {
                            System.out.println("Found user: " + person.getName());
                            return org.springframework.security.core.userdetails.User
                                    .withUsername(person.getName())
                                    .password(person.getPasswordHash()) // hashed password from DB
                                    .roles("USER")         // or whatever roles you want
                                    .build();
                        }
                )
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
