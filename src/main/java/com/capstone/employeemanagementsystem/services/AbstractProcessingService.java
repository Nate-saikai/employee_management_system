package com.capstone.employeemanagementsystem.services;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.sql.SQLIntegrityConstraintViolationException;

interface AbstractProcessingService {
    ResponseEntity<?> cdOps (String select, String param, Long num) throws SQLIntegrityConstraintViolationException;
    ResponseEntity<?> search(String search, Pageable pageable);
}
