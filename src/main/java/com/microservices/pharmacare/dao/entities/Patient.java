package com.microservices.pharmacare.dao.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codePatient;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String tel;

    @Column(nullable = true)
    private String motDePasse;

    @Column(nullable = false)
    private String cin;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String profilePictureUrl; // Add profile picture field

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Ordonnance> ordonnances;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Medicament> medicaments;

}
