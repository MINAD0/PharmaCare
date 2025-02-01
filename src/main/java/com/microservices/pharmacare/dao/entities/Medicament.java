package com.microservices.pharmacare.dao.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String image;

  @OneToMany(mappedBy = "medicament", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrdonnanceDetail> OrdonnanceDetail; // Liste des posologies et fréquences

    @OneToMany(mappedBy = "medicament", cascade = CascadeType.ALL)
    private List<Rappel> rappels;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToMany(mappedBy = "medicaments")
    private List<Ordonnance> ordonnances;
}
