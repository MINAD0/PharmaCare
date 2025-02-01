package com.microservices.pharmacare.service;

import com.microservices.pharmacare.dao.entities.Medicament;
import com.microservices.pharmacare.dao.repository.MedicamentRepository;
import com.microservices.pharmacare.dao.repository.OrdonnanceRepository;
import com.microservices.pharmacare.dto.MedicamentCreateDTO;
import com.microservices.pharmacare.dto.MedicamentDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicamentServiceImpl implements MedicamentService {

    private final MedicamentRepository medicamentRepository;
    private final OrdonnanceRepository ordonnanceRepository;

    public MedicamentServiceImpl(MedicamentRepository medicamentRepository, OrdonnanceRepository ordonnanceRepository) {
        this.medicamentRepository = medicamentRepository;
        this.ordonnanceRepository = ordonnanceRepository;
    }

    @Override
    public MedicamentDTO addMedicamentToOrdonnance(MedicamentCreateDTO medicamentCreateDTO) {
        return null;
    }

    @Override
    public List<MedicamentDTO> getMedicamentsByOrdonnance(Long ordonnanceId) {
        return null;
    }


    @Override
    public void deleteMedicament(Long id) {
        medicamentRepository.deleteById(id);
    }

}