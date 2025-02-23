package com.microservices.pharmacare.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RappelDTO {
    private Long id;
    private String message;
    private LocalDateTime dateRappel;
    private Boolean status; // ✅ Include status
}
