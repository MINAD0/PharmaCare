package com.microservices.pharmacare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrdonnanceMedicamentDTO {
    private Long medicamentId;
    private String medicamentName;
    private String posologie;
    private String frequence;
}
