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
public class Medicament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String description;

    @Lob
    @Column(columnDefinition = "LONGTEXT")  // Store large text data (Base64 encoded image)
    private String imageBase64;

    // ✅ Many-to-Many Relationship with Ordonnance using a Join Table
    @OneToMany(mappedBy = "medicament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdonnanceMedicament> ordonnanceMedicaments;
}
