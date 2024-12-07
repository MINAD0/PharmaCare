package com.microservices.pharmacare.dao.repository;

import com.microservices.pharmacare.dao.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByCodePatient(String codePatient);
    boolean existsByCodePatient(String codePatient);

}
