package com.microservices.pharmacare.dto;

import lombok.Data;

@Data
public class PatientCreateDto {
    private String codePatient;
    private String nom;
    private String prenom;
    private String tel;
    private String cin;
}
