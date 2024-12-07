package com.microservices.pharmacare.service;

import com.microservices.pharmacare.dao.entities.Patient;
import com.microservices.pharmacare.dao.entities.Pharmacien;
import com.microservices.pharmacare.dao.repository.PatientRepository;
import com.microservices.pharmacare.dao.repository.PharmacienRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final PharmacienRepository pharmacienRepository;
    private final PatientRepository patientRepository;

    public CustomUserDetailsService(PharmacienRepository pharmacienRepository, PatientRepository patientRepository) {
        this.pharmacienRepository = pharmacienRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrCode) throws UsernameNotFoundException {
        if (emailOrCode.equals("admin@example.com")){
            Pharmacien pharmacien = pharmacienRepository.findByEmail(emailOrCode)
                    .orElseThrow(() -> new UsernameNotFoundException("Pharmacien not found with email: " + emailOrCode));
            return new org.springframework.security.core.userdetails.User(pharmacien.getEmail(), pharmacien.getMotDePasse(), List.of(() -> "PHARMACIEN"));
        }else {
            Patient patient = patientRepository.findByCodePatient(emailOrCode)
                    .orElseThrow(() -> new UsernameNotFoundException("Patient not found with code: " + emailOrCode));
            return new org.springframework.security.core.userdetails.User(patient.getCodePatient(), patient.getMotDePasse(), List.of(() -> "PATIENT"));
        }
    }
}
