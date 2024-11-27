package com.microservices.pharmacare.dao.repository;

import com.microservices.pharmacare.dao.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findByCodePatient(String codePatient);
}
