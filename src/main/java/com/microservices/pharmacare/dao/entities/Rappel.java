package com.microservices.pharmacare.dao.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rappel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message; // ✅ The reminder message

    @Column(nullable = false)
    private LocalDateTime dateRappel; // ✅ When the reminder should be sent

    @Column(nullable = false)
    private Boolean status = false; // ✅ False by default (not acknowledged)

    @ManyToOne
    @JoinColumn(name = "ordonnance_id", nullable = false)
    private Ordonnance ordonnance;
}
