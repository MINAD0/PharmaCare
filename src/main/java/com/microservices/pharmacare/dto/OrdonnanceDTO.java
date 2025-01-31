package com.microservices.pharmacare.dto;

import com.microservices.pharmacare.dao.entities.Pharmacien;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdonnanceDTO {
    private Long id;
    private String description;
    private LocalDate date;
    private PatientDTO patient; // Ajout du patient
    private Pharmacien pharmacien;
    private List<MedicamentDTO> medicaments; // Ajout des médicaments

}
