package com.microservices.pharmacare.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrdonnanceCreateDTO {
    private String description;
    private String nom;
    private String codePatient;
    private List<OrdonnanceMedicamentDTO> medicaments; // List of medicaments with dosage
    private List<RappelDTO> rappels;
}
