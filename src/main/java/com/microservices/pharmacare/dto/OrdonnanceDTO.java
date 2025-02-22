package com.microservices.pharmacare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class OrdonnanceDTO {
    private Long id;
//    private String nom;
    private String description;
    private Date createdAt;
    private String codePatient;
    private List<OrdonnanceMedicamentDTO> medicaments; // List of Medicaments with specific dosage
}
