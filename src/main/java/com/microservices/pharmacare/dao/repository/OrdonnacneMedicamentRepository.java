package com.microservices.pharmacare.dao.repository;

import com.microservices.pharmacare.dao.entities.OrdonnanceMedicament;
import com.microservices.pharmacare.dao.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdonnacneMedicamentRepository extends JpaRepository<OrdonnanceMedicament,Long> {
    List<OrdonnanceMedicament> findByOrdonnanceId(Long ordonnanceId);
    List<OrdonnanceMedicament> findByOrdonnance_Patient(Patient patient);
}
