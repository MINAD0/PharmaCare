package com.microservices.pharmacare.dao.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
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

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // Automatically set when the entity is created

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt; // Automatically updated when the entity is updated

    // Add status column
    @Transient
    private int status;

    @PostLoad
    public void setStatus() {
        // Check if the password is set (not null or empty)
        if (this.motDePasse != null && !this.motDePasse.isEmpty()) {
            this.status = 1;
        } else {
            this.status = 0;
        }
    }

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Ordonnance> ordonnances;

//    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
//    private List<Medicament> medicaments;

}