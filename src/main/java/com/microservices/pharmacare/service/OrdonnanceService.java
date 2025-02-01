package com.microservices.pharmacare.service;

import com.microservices.pharmacare.dto.OrdonnanceCreateDTO;
import com.microservices.pharmacare.dto.OrdonnanceDTO;

import java.util.List;

public interface OrdonnanceService {
    OrdonnanceDTO createOrdonnance(OrdonnanceCreateDTO ordonnanceCreateDTO);
    OrdonnanceDTO getOrdonnanceById(Long id);
    List<OrdonnanceDTO> getAllOrdonnances();
    List<OrdonnanceDTO> getOrdonnancesByPatientCode(String codePatient);
    void deleteOrdonnance(Long id);
}
