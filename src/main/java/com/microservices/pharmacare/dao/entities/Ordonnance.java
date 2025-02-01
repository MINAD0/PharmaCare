package com.microservices.pharmacare.dao.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ordonnance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacien_id", nullable = true)
    private Pharmacien pharmacien;

    @OneToMany(mappedBy = "ordonnance", cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private List<Medicament> médicaments = new ArrayList<>();
}
