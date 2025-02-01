package com.microservices.pharmacare.dto;

import com.microservices.pharmacare.dao.entities.Rappel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentDTO {
    private Long id;
    private String nom;
    private String image;
    private Long ordonnanceId; // ID de l'ordonnance associée
    private Long patientId; // ID du patient associé
    private List<Rappel> rappels; // Liste des rappels associés
    private List<OrdonnanceDetailDTO> OrdonnanceDetail; // Liste des posologies et fréquences


}