package com.microservices.pharmacare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
@Data
@AllArgsConstructor
public class OrdonnanceDTO {
    private Long id;
    private String description;
    private LocalDate date;

}
