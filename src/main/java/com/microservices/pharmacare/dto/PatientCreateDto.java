package com.microservices.pharmacare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatientCreateDto {
    private String codePatient;
    private String nom;
    private String prenom;
    private String tel;
    private String cin;
    private String profilePictureUrl;
}
