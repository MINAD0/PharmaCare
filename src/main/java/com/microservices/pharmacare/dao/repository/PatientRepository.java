package com.microservices.pharmacare.dao.repository;

import com.microservices.pharmacare.dao.entities.Patient;
import com.microservices.pharmacare.dto.PatientDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByCodePatient(String codePatient);
    boolean existsByCodePatient(String codePatient);

    @Query("SELECT p FROM Patient p ORDER BY p.createdAt ASC")
    List<Patient> findAllByOrderByCreatedAtDesc();
}
