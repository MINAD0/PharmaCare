package com.microservices.pharmacare.service;

import com.microservices.pharmacare.dao.entities.*;
import com.microservices.pharmacare.dao.repository.*;
import com.microservices.pharmacare.dto.OrdonnanceCreateDTO;
import com.microservices.pharmacare.dto.OrdonnanceDTO;
import com.microservices.pharmacare.dto.OrdonnanceMedicamentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrdonnanceService {

    private final OrdonnanceRepository ordonnanceRepository;
    private final PatientRepository patientRepository;
    private final MedicamentRepository medicamentRepository;
    private final OrdonnacneMedicamentRepository ordonnanceMedicamentRepository;
    private final PharmacienRepository pharmacienRepository;

    @Autowired
    public OrdonnanceService(OrdonnanceRepository ordonnanceRepository, PatientRepository patientRepository, MedicamentRepository medicamentRepository, OrdonnacneMedicamentRepository ordonnacneMedicamentRepository, PharmacienRepository pharmacienRepository) {
        this.ordonnanceRepository = ordonnanceRepository;
        this.patientRepository = patientRepository;
        this.medicamentRepository = medicamentRepository;
        this.ordonnanceMedicamentRepository = ordonnacneMedicamentRepository;
        this.pharmacienRepository = pharmacienRepository;
    }

    public List<OrdonnanceDTO> getAllOrdonnancesByPatient(String codePatient) {
        Optional<Patient> patient = patientRepository.findByCodePatient(codePatient);
        if (patient.isPresent()) {
            return ordonnanceRepository.findByPatient(patient.get()).stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public OrdonnanceDTO createOrdonnance(OrdonnanceCreateDTO dto) {
        Optional<Patient> patient = patientRepository.findByCodePatient(dto.getCodePatient());
        Optional<Pharmacien> pharmacien = pharmacienRepository.findByEmail("admin@example.com");


        if (patient.isPresent() && pharmacien.isPresent()) {
            // ✅ Step 1: Create and Save the Ordonnance
            Ordonnance ordonnance = new Ordonnance();
            ordonnance.setDescription(dto.getDescription());
            ordonnance.setNom(dto.getNom());
            ordonnance.setPatient(patient.get());
            ordonnance.setPharmacien(pharmacien.get());

            Ordonnance savedOrdonnance = ordonnanceRepository.save(ordonnance);

            // ✅ Step 2: Link Existing Medicaments to the Ordonnance
            List<OrdonnanceMedicament> ordonnanceMedicaments = dto.getMedicaments().stream().map(medDto -> {
                Medicament medicament = medicamentRepository.findById(medDto.getMedicamentId())
                        .orElseThrow(() -> new RuntimeException("Medicament with ID " + medDto.getMedicamentId() + " not found."));

                OrdonnanceMedicament ordMed = new OrdonnanceMedicament();
                ordMed.setOrdonnance(savedOrdonnance);
                ordMed.setMedicament(medicament);
                ordMed.setPosologie(medDto.getPosologie());
                ordMed.setFrequence(medDto.getFrequence());

                return ordMed;
            }).collect(Collectors.toList());

            ordonnanceMedicamentRepository.saveAll(ordonnanceMedicaments);  // ✅ Save relations

            return mapToDto(savedOrdonnance);
        }
        throw new RuntimeException("Patient or Pharmacien not found");
    }

    private OrdonnanceDTO mapToDto(Ordonnance ordonnance) {
        List<OrdonnanceMedicamentDTO> medicamentDTOs = (ordonnance.getOrdonnanceMedicaments() != null)
                ? ordonnance.getOrdonnanceMedicaments().stream().map(ordMed ->
                new OrdonnanceMedicamentDTO(
                        ordMed.getMedicament().getId(),
                        ordMed.getMedicament().getNom(),
                        ordMed.getPosologie(),
                        ordMed.getFrequence()
                )
        ).collect(Collectors.toList()): new ArrayList<>();

        return new OrdonnanceDTO(
                ordonnance.getId(),
                ordonnance.getDescription(),
//                ordonnance.getNom(),
                ordonnance.getCreatedAt(),
                ordonnance.getPatient().getCodePatient(),
                medicamentDTOs
        );
    }



}
