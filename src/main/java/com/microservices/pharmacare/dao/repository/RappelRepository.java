package com.microservices.pharmacare.dao.repository;

import com.microservices.pharmacare.dao.entities.Medicament;
import com.microservices.pharmacare.dao.entities.Rappel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface RappelRepository extends JpaRepository<Rappel, Long> {
    List<Rappel> findByDateHeure(LocalTime heure);
//    List<Rappel> findByMessage(String message);
    List<Rappel> findByMedicament(Medicament medicament);

    List<Rappel> findByPatient_CodePatient(String codePatient);

}
