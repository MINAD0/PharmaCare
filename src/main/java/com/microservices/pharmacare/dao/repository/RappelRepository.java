package com.microservices.pharmacare.dao.repository;

import com.microservices.pharmacare.dao.entities.Medicament;
import com.microservices.pharmacare.dao.entities.Patient;
import com.microservices.pharmacare.dao.entities.Rappel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface RappelRepository extends JpaRepository<Rappel, Long> {

    public List<Rappel> findByOrdonnance_Patient_CodePatient(String codePatient);


}
