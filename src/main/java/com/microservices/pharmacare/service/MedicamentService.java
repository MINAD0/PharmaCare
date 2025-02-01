package com.microservices.pharmacare.service;

import com.microservices.pharmacare.dto.MedicamentCreateDTO;
import com.microservices.pharmacare.dto.MedicamentDTO;

import java.util.List;

public interface MedicamentService {
    MedicamentDTO addMedicamentToOrdonnance(MedicamentCreateDTO medicamentCreateDTO);
    List<MedicamentDTO> getMedicamentsByOrdonnance(Long ordonnanceId);
    void deleteMedicament(Long id);
}
