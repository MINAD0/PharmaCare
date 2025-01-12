package com.microservices.pharmacare.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RappelDTO {
    private String titre;
    private String description;
    private LocalDateTime dateHeure;
    private Long medicamentId;
    private String patientCode;
}
