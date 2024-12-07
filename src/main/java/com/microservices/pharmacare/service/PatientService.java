package com.microservices.pharmacare.service;

import com.microservices.pharmacare.dao.entities.Medicament;
import com.microservices.pharmacare.dao.entities.Ordonnance;
import com.microservices.pharmacare.dao.entities.Patient;

import com.microservices.pharmacare.dao.repository.MedicamentRepository;
import com.microservices.pharmacare.dao.repository.OrdonnanceRepository;
import com.microservices.pharmacare.dao.repository.PatientRepository;
import com.microservices.pharmacare.dto.OrdonnanceDTO;
import com.microservices.pharmacare.dto.PatientCreateDto;
import com.microservices.pharmacare.dto.PatientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // Obtenir toutes les ordonnances d'un patient par son ID
    public List<Ordonnance> getAllOrdonnancesByPatientId(Long patientId) {
        List<Ordonnance> ordonnances = ordonnanceRepository.findByPatientId(patientId);
        if (ordonnances.isEmpty()) {
            throw new IllegalArgumentException("No ordonnance found for this patient");
        }
        return ordonnances;
    }

    // Obtenir les détails d'un patient par son ID
    public Patient getPatientById(Long patientId) {
        return patientRepository.findById(patientId).orElse(null);
    }

    // Mettre à jour les informations d'un patient
    public Patient updatePatient(Long patientId, PatientCreateDto patientUpdateDto) {
        Patient existingPatient = patientRepository.findById(patientId).orElse(null);
        if (existingPatient != null) {
            existingPatient.setNom(patientUpdateDto.getNom());
            existingPatient.setPrenom(patientUpdateDto.getPrenom());
            existingPatient.setTel(patientUpdateDto.getTel());
            existingPatient.setCin(patientUpdateDto.getCin());
            return patientRepository.save(existingPatient);
        }
        return null;
    }

    // Supprimer un patient
    public void deletePatient(Long patientId) {
        if (patientRepository.existsById(patientId)){
            patientRepository.deleteById(patientId);
        }else{
            throw new IllegalArgumentException("Patient not found");
        }
    }

    public PatientDTO getPatientByCode(String codePatient) {
        Optional<Patient> patient = patientRepository.findByCodePatient(codePatient);
        return patient.map(this::mapToDto).orElse(null);
    }

    private PatientDTO mapToDto(Patient patient) {
        List<OrdonnanceDTO> ordonnances = patient.getOrdonnances().stream()
                .map(ordonnance -> new OrdonnanceDTO(ordonnance.getId(), ordonnance.getDescription(), ordonnance.getDate()))
                .collect(Collectors.toList());
        return new PatientDTO(
                patient.getCodePatient(),
                patient.getNom(),
                patient.getPrenom(),
                patient.getTel(),
                patient.getCin(),
                ordonnances
        );
    }



    // Obtenir l'historique des médicaments consommés par un patient
    public List<Medicament> getHistoriqueMédicamentsByPatientId(Long patientId) {
        List<Medicament> medicaments = medicamentRepository.findByPatientId(patientId);
        if (medicaments.isEmpty()) {
            throw new IllegalArgumentException("No medicament found for this patient");
        }
        return medicaments;
    }

    public boolean verifyPatientCode(String codePatient) {
        return patientRepository.existsByCodePatient(codePatient);
    }

}