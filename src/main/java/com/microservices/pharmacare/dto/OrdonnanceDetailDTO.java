package com.microservices.pharmacare.dto;

import lombok.*;

import java.util.List;
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdonnanceDetailDTO {
    private Long id;
    private String posologie;
    private String frequence;
    private Long medicamentId;
    private Long ordonnanceId;

}
