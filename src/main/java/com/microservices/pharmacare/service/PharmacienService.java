package com.microservices.pharmacare.service;

import com.microservices.pharmacare.dao.entities.Patient;
import com.microservices.pharmacare.dao.repository.OrdonnanceDetailRepository;
import com.microservices.pharmacare.dao.repository.OrdonnanceRepository;
import com.microservices.pharmacare.dao.repository.PatientRepository;
import com.microservices.pharmacare.dao.repository.PharmacienRepository;
import com.microservices.pharmacare.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PharmacienService {

    private final PharmacienRepository pharmacienRepository;
    private final OrdonnanceDetailRepository ordonnanceDetailRepository;
    private final PatientRepository patientRepository;
    private final OrdonnanceRepository ordonnanceRepository;
    private final SmsService smsService;

    public PharmacienService(PharmacienRepository pharmacienRepository, OrdonnanceDetailRepository ordonnanceDetailRepository, PatientRepository patientRepository, OrdonnanceRepository ordonnanceRepository, SmsService smsService) {
        this.pharmacienRepository = pharmacienRepository;
        this.ordonnanceDetailRepository = ordonnanceDetailRepository;
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

    public List<PatientDTO> ListPatients() {
        try{
            return patientRepository.findAllByOrderByCreatedAtDesc().stream().map(this::mapToDto).collect(Collectors.toList());
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

    private PatientDTO mapToDto(Patient patient) {
        // Vérifier si le mot de passe est défini pour déterminer le statut
        int status = (patient.getMotDePasse() != null && !patient.getMotDePasse().isEmpty()) ? 1 : 0;

        // Mapper les ordonnances du patient en OrdonnanceDTO
        List<OrdonnanceDTO> ordonnances = patient.getOrdonnances().stream()
                .map(ordonnance -> new OrdonnanceDTO(
                        ordonnance.getId(),
                        ordonnance.getDescription(),
                        ordonnance.getDate(),
                        null, // Éviter la récursion infinie en mettant null pour le patient
                        ordonnance.getPharmacien(),
                        ordonnance.getMedicaments().stream()
                                .map(medicament -> new MedicamentDTO(
                                        medicament.getId(),
                                        medicament.getNom(),
                                        medicament.getImage(),
                                        ordonnance.getId(),
                                        patient.getCodePatient() != null ? Long.parseLong(patient.getCodePatient()) : null,
                                        medicament.getRappels(),
                                        null // ordonnanceDetail
                                ))

                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        // Retourner l'objet PatientDTO avec les ordonnances correctement mappées
        return new PatientDTO(
                patient.getCodePatient(),
                patient.getNom(),
                patient.getPrenom(),
                patient.getTel(),
                patient.getCin(),
                patient.getProfilePictureUrl(),
                ordonnances, // Liste des ordonnances mappées
                patient.getCreatedAt(),
                patient.getUpdatedAt(),
                status
        );
    }

    public List<OrdonnanceDTO> listOrdonnance() {
        try {
            return ordonnanceRepository.findAll().stream()
                    .map(ordonnance -> new OrdonnanceDTO(
                            ordonnance.getId(),
                            ordonnance.getDescription(),
                            ordonnance.getDate(),
                            null, // Éviter la récursion infinie
                            ordonnance.getPharmacien(),
                            ordonnance.getMedicaments().stream()
                                    .map(medicament -> {
                                        // Récupérer les détails de l'ordonnance associés au médicament
                                        List<OrdonnanceDetailDTO> ordonnanceDetails = ordonnanceDetailRepository.findByMedicament(medicament).stream()
                                                .map(detail -> new OrdonnanceDetailDTO(
                                                        detail.getId(),
                                                        detail.getPosologie(),
                                                        detail.getFrequence(),
                                                        medicament.getId(),
                                                        ordonnance.getId()
                                                ))
                                                .toList();

                                        return new MedicamentDTO(
                                                medicament.getId(),
                                                medicament.getNom(),
                                                medicament.getImage(),
                                                ordonnance.getId(),
                                                null, // Pas de patient ici
                                                medicament.getRappels(),
                                                ordonnanceDetails // Liste des détails
                                        );
                                    })
                                    .toList()
                    ))
                    .toList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Retourner une liste vide en cas d'erreur
        }
    }

    public List<PatientDTO> getPatientOrdonnances(String codePatient) {
        try {
            Patient patient = patientRepository.findByCodePatient(codePatient).orElse(null);
            if (patient == null) {
                return List.of(); // Retourner une liste vide si aucun patient n'est trouvé
            }

            int status = (patient.getMotDePasse() != null && !patient.getMotDePasse().isEmpty()) ? 1 : 0;

            List<OrdonnanceDTO> ordonnances = patient.getOrdonnances().stream()
                    .map(ordonnance -> new OrdonnanceDTO(
                            ordonnance.getId(),
                            ordonnance.getDescription(),
                            ordonnance.getDate(),
                            null, // Éviter la récursion infinie
                            ordonnance.getPharmacien(),
                            ordonnance.getMedicaments().stream()
                                    .map(medicament -> {
                                        // Récupération sécurisée du codePatient
                                        Long patientId = null;
                                        if (patient.getCodePatient() != null && !patient.getCodePatient().isEmpty()) {
                                            try {
                                                patientId = Long.parseLong(patient.getCodePatient());
                                            } catch (NumberFormatException e) {
                                                patientId = null; // Gérer les erreurs de conversion
                                            }
                                        }

                                        // Récupérer les détails de l'ordonnance pour le médicament
                                        List<OrdonnanceDetailDTO> ordonnanceDetails = ordonnanceDetailRepository.findByMedicament(medicament).stream()
                                                .map(detail -> new OrdonnanceDetailDTO(
                                                        detail.getId(),
                                                        detail.getPosologie(),
                                                        detail.getFrequence(),
                                                        medicament.getId(),
                                                        ordonnance.getId()
                                                ))
                                                .toList();

                                        return new MedicamentDTO(
                                                medicament.getId(),
                                                medicament.getNom(),
                                                medicament.getImage(),
                                                ordonnance.getId(),
                                                patientId,
                                                medicament.getRappels(),
                                                ordonnanceDetails // Liste des détails
                                        );
                                    })
                                    .toList()
                    ))
                    .toList();

            // Construire et retourner le DTO du patient
            return List.of(new PatientDTO(
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
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Retourner une liste vide en cas d'erreur
        }
    }


    public void deletePatientByCode(String codePatient) {
        Optional<Patient> patient = patientRepository.findByCodePatient(codePatient);
        if (patient.isPresent()) {
            patientRepository.delete(patient.get());
        } else {
            throw new IllegalArgumentException("Patient not found with code: " + codePatient);
        }
    }

}
