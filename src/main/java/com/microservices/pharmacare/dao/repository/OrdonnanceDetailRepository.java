package com.microservices.pharmacare.dao.repository;

import com.microservices.pharmacare.dao.entities.OrdonnanceDetail;
import com.microservices.pharmacare.dao.entities.Medicament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdonnanceDetailRepository extends JpaRepository<OrdonnanceDetail, Long> {
    List<OrdonnanceDetail> findByOrdonnanceId(Long ordonnanceId);
    List<OrdonnanceDetail> findByMedicamentId(Long medicamentId);
    Optional<OrdonnanceDetail> findByMedicament(Medicament medicament);
}
