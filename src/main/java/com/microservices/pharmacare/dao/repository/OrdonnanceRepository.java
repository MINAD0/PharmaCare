package com.microservices.pharmacare.dao.repository;

import com.microservices.pharmacare.dao.entities.Ordonnance;
import com.microservices.pharmacare.dao.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OrdonnanceRepository extends JpaRepository<Ordonnance, Long> {
    // Méthode pour obtenir tous les médicaments d'un patient par son ID
    List<Ordonnance> findByPatientId(Long patientId);

    Collection<Ordonnance> findByPatient(Patient patient);
}
