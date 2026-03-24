package com.example.demo.service;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.Patient;
import com.example.demo.model.Role;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public PatientService(PatientRepository patientRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerPatient(RegisterRequest request) {
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String email = request.getEmail() == null ? "" : request.getEmail().trim();

        if (patientRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (patientRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        Role patientRole = roleRepository.findByName("PATIENT")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("PATIENT");
                    return roleRepository.save(role);
                });
        Patient patient = new Patient();
        patient.setUsername(username);
        patient.setPassword(passwordEncoder.encode(request.getPassword()));
        patient.setEmail(email);
        patient.setRoles(Set.of(patientRole));
        patientRepository.save(patient);
    }
}
