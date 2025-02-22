package com.microservices.pharmacare.dao.repository;

import com.microservices.pharmacare.dao.entities.Medicament;
import com.microservices.pharmacare.dao.entities.Ordonnance;
import com.microservices.pharmacare.dao.entities.Patient;
import com.microservices.pharmacare.dto.MedicamentDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicamentRepository extends JpaRepository<Medicament, Long> {
    Medicament findByNom(String nom);
//    List<Medicament> findByPatient(Patient patient);
//    List<Medicament> findByOrdonnance(Ordonnance ordonnance);

}
