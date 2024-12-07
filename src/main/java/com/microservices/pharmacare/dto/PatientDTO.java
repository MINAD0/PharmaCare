package com.microservices.pharmacare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PatientDTO {
    private String codePatient;
    private String nom;
    private String prenom;
    private String tel;
    private String cin;
    private List<OrdonnanceDTO> ordonnances;
}
