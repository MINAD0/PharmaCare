package com.microservices.pharmacare.service;

import com.microservices.pharmacare.dao.entities.Medicament;
import com.microservices.pharmacare.dao.entities.Ordonnance;
import com.microservices.pharmacare.dao.entities.Patient;

import com.microservices.pharmacare.dao.repository.MedicamentRepository;
import com.microservices.pharmacare.dao.repository.OrdonnanceRepository;
import com.microservices.pharmacare.dao.repository.PatientRepository;
import com.microservices.pharmacare.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final OrdonnanceRepository ordonnanceRepository;
    private final MedicamentRepository medicamentRepository;
    private final PharmacienService pharmacienService;

    @Autowired
    public PatientService(PatientRepository patientRepository, OrdonnanceRepository ordonnanceRepository, MedicamentRepository medicamentRepository, PharmacienService pharmacienService) {
        this.patientRepository = patientRepository;
        this.ordonnanceRepository = ordonnanceRepository;
        this.medicamentRepository = medicamentRepository;
        this.pharmacienService = pharmacienService;
    }

    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<Ordonnance> getOrdonnancesByCodePatient(String codePatient) {
        Optional<Patient> patient = patientRepository.findByCodePatient(codePatient);
        if (patient.isPresent()) {
            return patient.get().getOrdonnances(); // Assuming `ordonnances` is a field in `Patient`
        } else {
            return Collections.emptyList(); // Return empty list if no patient is found
        }
    }


    public PatientDTO updatePatient(String codePatient, PatientCreateDto patientUpdateDto) {
        Optional<Patient> existingPatient = patientRepository.findByCodePatient(codePatient);
        if (existingPatient.isPresent()) {
            Patient patient = existingPatient.get();
            patient.setNom(patientUpdateDto.getNom());
            patient.setPrenom(patientUpdateDto.getPrenom());
            patient.setTel(patientUpdateDto.getTel());
            patient.setCin(patientUpdateDto.getCin());
            patient.setProfilePictureUrl(patientUpdateDto.getProfilePictureUrl()); // Add this line

//            System.out.println("Received profilePicture: " + patientUpdateDto.getProfilePictureUrl()); // Log profile picture

            Patient updatedPatient = patientRepository.save(patient);
            return mapToDto(updatedPatient);
        }
        throw new IllegalArgumentException("Patient with code " + codePatient + " not found.");
    }




    public void deletePatientByCode(String codePatient) {
        Optional<Patient> patient = patientRepository.findByCodePatient(codePatient);
        if (patient.isPresent()) {
            patientRepository.delete(patient.get());
        } else {
            throw new IllegalArgumentException("Patient not found with code: " + codePatient);
        }
    }


    public PatientDTO getPatientByCode(String codePatient) {
        Optional<Patient> patient = patientRepository.findByCodePatient(codePatient);
        return patient.map(this::mapToDto).orElse(null);
    }

    private PatientDTO mapToDto(Patient patient) {
        if (patient == null) {
            return null;
        }

        int status = (patient.getMotDePasse() != null && !patient.getMotDePasse().isEmpty()) ? 1 : 0;

        List<OrdonnanceDTO> ordonnances = (patient.getOrdonnances() != null)
                ? patient.getOrdonnances().stream()
                .map(ordonnance -> new OrdonnanceDTO(
                        ordonnance.getId(),
                        ordonnance.getDescription(),
                        ordonnance.getDate(),
                        null, // Éviter la récursivité avec patientDTO
                        ordonnance.getPharmacien(),
                        ordonnance.getMedicaments() != null
                                ? ordonnance.getMedicaments().stream()
                                .map(medicament -> {
                                    // Récupérer les détails de l'ordonnance associés à ce médicament
                                    List<OrdonnanceDetailDTO> ordonnanceDetailDTOs = medicament.getOrdonnanceDetail() != null
                                            ? medicament.getOrdonnanceDetail().stream()
                                            .map(detail -> new OrdonnanceDetailDTO(
                                                    detail.getId(),
                                                    detail.getPosologie(),
                                                    detail.getFrequence(),
                                                    medicament.getId(),
                                                    ordonnance.getId()
                                            ))
                                            .toList()
                                            : List.of();

                                    // Convertir `patient.getCodePatient()` en Long en toute sécurité
                                    Long patientId = null;
                                    if (patient.getCodePatient() != null && !patient.getCodePatient().isEmpty()) {
                                        try {
                                            patientId = Long.parseLong(patient.getCodePatient());
                                        } catch (NumberFormatException e) {
                                            patientId = null; // En cas d'erreur, on garde `null`
                                        }
                                    }

                                    return new MedicamentDTO(
                                            medicament.getId(),
                                            medicament.getNom(),
                                            medicament.getImage(),
                                            ordonnance.getId(),
                                            patientId,
                                            medicament.getRappels(),
                                            ordonnanceDetailDTOs // Liste des posologies et fréquences
                                    );
                                })
                                .toList()
                                : List.of()
                ))
                .toList()
                : List.of();

        return new PatientDTO(
                patient.getCodePatient(),
                patient.getNom(),
                patient.getPrenom(),
                patient.getTel(),
                patient.getCin(),
                patient.getProfilePictureUrl(),
                ordonnances,
                patient.getCreatedAt(),
                patient.getUpdatedAt(),
                status
        );
    }


    public List<Medicament> getHistoriqueMedicamentsByCodePatient(String codePatient) {
        Optional<Patient> patient = patientRepository.findByCodePatient(codePatient);
        if (patient.isPresent()) {
            return medicamentRepository.findByPatientId(patient.get().getId());
        } else {
            throw new IllegalArgumentException("No patient found with code: " + codePatient);
        }
    }


    public boolean verifyPatientCode(String codePatient) {
        return patientRepository.existsByCodePatient(codePatient);
    }

    public boolean isPasswordSet(String codePatient) {
        Optional<Patient> patient = patientRepository.findByCodePatient(codePatient);
        return patient.isPresent() && patient.get().getMotDePasse() != null && !patient.get().getMotDePasse().isEmpty();
    }


}