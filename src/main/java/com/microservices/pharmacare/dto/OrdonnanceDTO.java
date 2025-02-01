package com.microservices.pharmacare.dto;

import com.microservices.pharmacare.dao.entities.Ordonnance;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class OrdonnanceDTO {
    private Long id;
    private String description;
    private LocalDate date;
    private String codePatient;  // Patient code (this could be a reference to the Patient)
    private List<MedicamentCreateDTO> medicaments;


}
