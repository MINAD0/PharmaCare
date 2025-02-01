package com.microservices.pharmacare.dto;

import com.microservices.pharmacare.dao.entities.Medicament;
import lombok.Data;

@Data
public class MedicamentDTO {
    private Long id;
    private String nom;
    private String posologie;
    private String fréquence;
    private Long ordonnanceId;

}
