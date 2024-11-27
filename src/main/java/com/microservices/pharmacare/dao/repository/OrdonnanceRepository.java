package com.microservices.pharmacare.dao.repository;

import com.microservices.pharmacare.dao.entities.Ordonnance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdonnanceRepository extends JpaRepository<Ordonnance, Long> {
}
