package com.microservices.pharmacare.service;

import com.microservices.pharmacare.dao.entities.*;
import com.microservices.pharmacare.dao.repository.*;
import com.microservices.pharmacare.dto.OrdonnanceCreateDTO;
import com.microservices.pharmacare.dto.OrdonnanceDTO;
import com.microservices.pharmacare.dto.OrdonnanceMedicamentDTO;
import com.microservices.pharmacare.dto.RappelDTO;
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
    private final RappelRepository rappelRepository;

    @Autowired
    public OrdonnanceService(OrdonnanceRepository ordonnanceRepository, PatientRepository patientRepository, MedicamentRepository medicamentRepository, OrdonnacneMedicamentRepository ordonnacneMedicamentRepository, PharmacienRepository pharmacienRepository, RappelRepository rappelRepository) {
        this.ordonnanceRepository = ordonnanceRepository;
        this.patientRepository = patientRepository;
        this.medicamentRepository = medicamentRepository;
        this.ordonnanceMedicamentRepository = ordonnacneMedicamentRepository;
        this.pharmacienRepository = pharmacienRepository;
        this.rappelRepository = rappelRepository;
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

            // ✅ Step 3: Save Rappels
            if (dto.getRappels() != null && !dto.getRappels().isEmpty()) {
                List<Rappel> rappels = dto.getRappels().stream()
                        .map(rappelDto -> Rappel.builder()
                                .message(rappelDto.getMessage())
                                .dateRappel(rappelDto.getDateRappel())
                                .status(rappelDto.getStatus() != null ? rappelDto.getStatus() : false) // ✅ Ensure status is not null
                                .ordonnance(savedOrdonnance)
                                .build())
                        .collect(Collectors.toList());

                rappelRepository.saveAll(rappels);
            }

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

        List<RappelDTO> rappelDTOs = ordonnance.getRappels().stream()
                .map(rappel -> new RappelDTO(
                        rappel.getId(),
                        rappel.getMessage(),
                        rappel.getDateRappel(),
                        rappel.getStatus()
                ))
                .collect(Collectors.toList());

        return new OrdonnanceDTO(
                ordonnance.getId(),
                ordonnance.getDescription(),
//                ordonnance.getNom(),
                ordonnance.getCreatedAt(),
                ordonnance.getPatient().getCodePatient(),
                medicamentDTOs,
                rappelDTOs
        );
    }

    public List<RappelDTO> getRappelsByPatient(String codePatient) {
        Optional<Patient> patient = patientRepository.findByCodePatient(codePatient);

        if (patient.isPresent()) {
            List<Rappel> rappels = rappelRepository.findByOrdonnance_Patient_CodePatient(patient.get().getCodePatient());

            return rappels.stream().map(rappel -> new RappelDTO(
                    rappel.getId(),
                    rappel.getMessage(),
                    rappel.getDateRappel(),
                    rappel.getStatus()
            )).collect(Collectors.toList());
        }

        return Collections.emptyList(); // Return an empty list if no patient found
    }

    public void updateRappelStatus(Long rappelId, Boolean newStatus) {
        Optional<Rappel> optionalRappel = rappelRepository.findById(rappelId);

        if (optionalRappel.isPresent()) {
            Rappel rappel = optionalRappel.get();

            // ✅ Ensure status is never null
            rappel.setStatus(newStatus != null ? newStatus : false);

            rappelRepository.save(rappel);
        } else {
            throw new RuntimeException("Rappel with ID " + rappelId + " not found.");
        }
    }

}
