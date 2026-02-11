package com.capstone.employeemanagementsystem.services;

import com.capstone.employeemanagementsystem.repositories.ManagerRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ManagerRepository managerRepository;

    public CustomUserDetailsService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String managerId) throws UsernameNotFoundException {
        System.out.println("Attempting login for person id: " + managerId);

        return managerRepository.findManagerByEmployeeId(managerId)
                .map(manager -> {
                    System.out.println("Found user: " + manager.getName());
                    return org.springframework.security.core.userdetails.User
                            .withUsername(manager.getEmployeeId())
                            .password(manager.getPasswordHash()) // hashed password from DB
                            .roles("ADMIN")
                            .build();
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + managerId));
    }


}
