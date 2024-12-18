package com.microservices.pharmacare.dao.repository;

import com.microservices.pharmacare.dao.entities.Pharmacien;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PharmacienRepository extends JpaRepository<Pharmacien, Long> {
    Optional<Pharmacien> findByEmail(String email);
}
