package com.microservices.pharmacare.dao.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // Automatically set when the entity is created

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt; // Automatically updated when the entity is updated

    @ManyToOne
    @JoinColumn(name = "pharmacien_id", nullable = false)
    private Pharmacien pharmacien;

    @OneToMany(mappedBy = "ordonnance", cascade = CascadeType.ALL)
    private List<Medicament> medicaments;
}
