package com.microservices.pharmacare.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Data
public class OrdonnanceCreateDTO {
    private String description;
    private LocalDate date;
    private String codePatient; // Patient code to associate with the ordonnance
    private List<MedicamentCreateDTO> medicaments;
}