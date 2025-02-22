package com.microservices.pharmacare.service;

import com.microservices.pharmacare.dao.entities.Medicament;
import com.microservices.pharmacare.dao.entities.Ordonnance;
import com.microservices.pharmacare.dao.entities.Patient;
import com.microservices.pharmacare.dao.entities.Pharmacien;
import com.microservices.pharmacare.dao.repository.MedicamentRepository;
import com.microservices.pharmacare.dao.repository.OrdonnanceRepository;
import com.microservices.pharmacare.dao.repository.PatientRepository;
import com.microservices.pharmacare.dao.repository.PharmacienRepository;
import com.microservices.pharmacare.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PharmacienService {

    private final PharmacienRepository pharmacienRepository;
    private final MedicamentRepository medicamentRepository;
    private final PatientRepository patientRepository;
    private final OrdonnanceRepository ordonnanceRepository;
    private final SmsService smsService;

    public PharmacienService(PharmacienRepository pharmacienRepository, MedicamentRepository medicamentRepository, PatientRepository patientRepository, OrdonnanceRepository ordonnanceRepository, SmsService smsService) {
        this.pharmacienRepository = pharmacienRepository;
        this.medicamentRepository = medicamentRepository;
        this.patientRepository = patientRepository;
        this.ordonnanceRepository = ordonnanceRepository;
        this.smsService = smsService;
    }

    public Patient createPatient(PatientCreateDto patientCreateDto) {
        try{
            Patient patient = new Patient();
            // Generate a simple codePatient
            String codePatient = generatePatientCode(patientCreateDto.getNom(), patientCreateDto.getPrenom());
            patient.setCodePatient(codePatient);
            patient.setNom(patientCreateDto.getNom());
            patient.setPrenom(patientCreateDto.getPrenom());
            patient.setTel(patientCreateDto.getTel());
            patient.setCin(patientCreateDto.getCin());
            patient.setMotDePasse(null); // Password is null by default

            Patient savedPatient = patientRepository.save(patient);
            // Send SMS with the patient code
            String message = String.format(
                    "Bonjour %s %s,\n" +
                            "Bienvenue chez PharmaCare, votre solution de santé personnalisée.\n" +
                            "Votre code patient est : %s\n" +
                            "Veuillez l'utiliser pour accéder à votre espace sécurisé.\n\n" +
                            "Merci de nous faire confiance !\n\n" +
                            "مرحبًا %s %s،\n" +
                            "مرحبًا بكم في PharmaCare، الحل الصحي المخصص لكم.\n" +
                            "رمز المريض الخاص بكم هو: %s\n" +
                            "يرجى استخدامه للوصول إلى مساحتكم الآمنة.\n\n" +
                            "شكرًا لثقتكم بنا!",
                    patient.getPrenom(), patient.getNom(), codePatient,
                    patient.getPrenom(), patient.getNom(), codePatient
            );
            smsService.sendSms(patient.getTel(), message);
            return savedPatient;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Patient> ListPatients() {
        try{
            return patientRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String generatePatientCode(String nom, String prenom) {
        String codePatient;
        do {
            String initials = (nom.substring(0, 1) + prenom.substring(0, 1)).toUpperCase();
            int randomNumber = (int) (Math.random() * 9000) + 1000;
            codePatient = initials + randomNumber;
        } while (patientRepository.existsByCodePatient(codePatient)); // Check uniqueness
        return codePatient;
    }

    public Optional<PatientDTO> getPatientByCode(String codePatient) {
        try{
            Optional<Patient> patient = patientRepository.findByCodePatient(codePatient);
            return patient.map(this::mapToDto);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public PatientDTO mapToDto(Patient patient) {
        // Convert ordonnances to DTO, including medicaments with posologie & fréquence
        List<OrdonnanceDTO> ordonnances = patient.getOrdonnances().stream()
                .map(ordonnance -> {
                    // Convert each OrdonnanceMedicament into DTO
                    List<OrdonnanceMedicamentDTO> medicamentDTOs = ordonnance.getOrdonnanceMedicaments().stream()
                            .map(ordMed -> new OrdonnanceMedicamentDTO(
                                    ordMed.getMedicament().getId(),
                                    ordMed.getMedicament().getNom(),
                                    ordMed.getPosologie(), // Retrieve posologie
                                    ordMed.getFrequence() // Retrieve fréquence
                            ))
                            .collect(Collectors.toList());

                    // Create and return the OrdonnanceDTO
                    return new OrdonnanceDTO(
                            ordonnance.getId(),
                            ordonnance.getDescription(),
//                            ordonnance.getNom(),
                            ordonnance.getCreatedAt(),
                            ordonnance.getPatient().getCodePatient(),
                            medicamentDTOs // Pass the list of medicamentDTOs here
                    );
                })
                .collect(Collectors.toList());

        return new PatientDTO(
                patient.getCodePatient(),
                patient.getNom(),
                patient.getPrenom(),
                patient.getTel(),
                patient.getCin(),
                patient.getProfilePictureUrl(),
                ordonnances
        );
    }

    // ✅ Fetch all ordonnances with corresponding medicaments
    public List<OrdonnanceDTO> listOrdonnance() {
        try {
            return ordonnanceRepository.findAll().stream()
                    .map(ordonnance -> {
                        // Map the Medicament list for the ordonnance to OrdonnanceMedicamentDTO
                        List<OrdonnanceMedicamentDTO> medicamentDTOs = ordonnance.getOrdonnanceMedicaments().stream()
                                .map(ordMed -> new OrdonnanceMedicamentDTO(
                                        ordMed.getMedicament().getId(),
                                        ordMed.getMedicament().getNom(),
                                        ordMed.getPosologie(),
                                        ordMed.getFrequence()
                                ))
                                .collect(Collectors.toList());

                        // Return OrdonnanceDTO with the list of MedicamentDTOs
                        return new OrdonnanceDTO(
                                ordonnance.getId(),
                                ordonnance.getDescription(),
//                                ordonnance.getNom(),
                                ordonnance.getCreatedAt(),
                                ordonnance.getPatient().getCodePatient(),
                                medicamentDTOs
                        );
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ✅ Fetch ordonnances of a specific patient with medicaments & their details
    public List<PatientDTO> getPatientOrdonnances(String codePatient) {
        try {
            Optional<Patient> patient = patientRepository.findByCodePatient(codePatient);
            if (patient.isPresent()) {
                // Map the ordonnances of the patient to OrdonnanceDTO
                List<OrdonnanceDTO> ordonnanceDTOs = patient.get().getOrdonnances().stream()
                        .map(ordonnance -> {
                            // Convert each Medicament to OrdonnanceMedicamentDTO
                            List<OrdonnanceMedicamentDTO> medicamentDTOs = ordonnance.getOrdonnanceMedicaments().stream()
                                    .map(ordMed -> new OrdonnanceMedicamentDTO(
                                            ordMed.getMedicament().getId(),
                                            ordMed.getMedicament().getNom(),
                                            ordMed.getPosologie(),
                                            ordMed.getFrequence()
                                    ))
                                    .collect(Collectors.toList());

                            // Return OrdonnanceDTO with all medicament details
                            return new OrdonnanceDTO(
                                    ordonnance.getId(),
                                    ordonnance.getDescription(),
//                                    ordonnance.getNom(),
                                    ordonnance.getCreatedAt(),
                                    ordonnance.getPatient().getCodePatient(),
                                    medicamentDTOs
                            );
                        })
                        .collect(Collectors.toList());

                // Create and return a single PatientDTO with all ordonnances
                Patient patientEntity = patient.get();
                PatientDTO patientDTO = new PatientDTO(
                        patientEntity.getCodePatient(),
                        patientEntity.getNom(),
                        patientEntity.getPrenom(),
                        patientEntity.getTel(),
                        patientEntity.getCin(),
                        patientEntity.getProfilePictureUrl(),
                        ordonnanceDTOs // Include all ordonnanceDTOs in the list
                );

                return List.of(patientDTO);
            } else {
                return null; // Or handle error if patient is not found
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
