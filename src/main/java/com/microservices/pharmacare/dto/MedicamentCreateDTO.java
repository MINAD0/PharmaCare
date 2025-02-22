package com.microservices.pharmacare.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MedicamentCreateDTO {
    private String nom;
    private String description;
    private MultipartFile imageFile; // File input for image
}
