package com.microservices.pharmacare.dto;

import lombok.Data;

@Data
public class MedicamentCreateDTO {
    private String nom;
    private String posologie;
    private String frequence;

    public MedicamentCreateDTO(String nom, String posologie, String fréquence) {
        this.nom = nom;
        this.posologie = posologie;
        this.frequence = fréquence;
    }
}