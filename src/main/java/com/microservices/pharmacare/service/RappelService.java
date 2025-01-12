package com.microservices.pharmacare.service;

import com.microservices.pharmacare.dao.entities.Medicament;
import com.microservices.pharmacare.dao.entities.Patient;
import com.microservices.pharmacare.dao.entities.Rappel;
import com.microservices.pharmacare.dao.repository.MedicamentRepository;
import com.microservices.pharmacare.dao.repository.PatientRepository;
import com.microservices.pharmacare.dao.repository.RappelRepository;
import com.microservices.pharmacare.dto.RappelDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RappelService {

    private final RappelRepository rappelRepository;
    private final PatientRepository patientRepository;
    private final MedicamentRepository medicamentRepository;


    public RappelService(RappelRepository rappelRepository, PatientRepository patientRepository, MedicamentRepository medicamentRepository) {
        this.rappelRepository = rappelRepository;
        this.patientRepository = patientRepository;
        this.medicamentRepository = medicamentRepository;
    }

    public Rappel createRappel(RappelDTO rappelDTO) {
        Patient patient = patientRepository.findByCodePatient(rappelDTO.getPatientCode())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        Medicament medicament = medicamentRepository.findById(rappelDTO.getMedicamentId())
                .orElseThrow(() -> new IllegalArgumentException("Medicament not found"));

        Rappel rappel = Rappel.builder()
                .titre(rappelDTO.getTitre())
                .description(rappelDTO.getDescription())
                .dateHeure(rappelDTO.getDateHeure())
                .patient(patient)
                .medicament(medicament)
                .build();
        return rappelRepository.save(rappel);
    }

    public List<RappelDTO> getRappelsByPatient(String codePatient) {
        return rappelRepository.findByPatient_CodePatient(codePatient).stream()
                .map(r -> new RappelDTO(
                        r.getTitre(),
                        r.getDescription(),
                        r.getDateHeure(),
                        r.getMedicament().getId(),
                        r.getPatient().getCodePatient()
                ))
                .collect(Collectors.toList());
    }


}
