package com.microservices.pharmacare.dao.repository;

import com.microservices.pharmacare.dao.entities.Pharmacien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PharmacienRepository extends JpaRepository<Pharmacien, Long> {
    Pharmacien findByEmail(String email);
}
