package com.microservices.pharmacare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicamentDTO {
    private Long id;
    private String nom;
    private String description;
    private String imageUrl; // Added image field
}

