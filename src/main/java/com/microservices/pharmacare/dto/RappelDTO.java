package com.microservices.pharmacare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RappelDTO {
    private Long id;
    private String codePatient;
    private Long medicamentId;
    private String message;
    private LocalDateTime dateHeure;
    private boolean etat;
}
