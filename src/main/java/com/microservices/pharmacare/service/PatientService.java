package com.microservices.pharmacare.service;

import com.microservices.pharmacare.dao.entities.Medicament;
import com.microservices.pharmacare.dao.entities.Ordonnance;
import com.microservices.pharmacare.dao.entities.Patient;

import com.microservices.pharmacare.dao.repository.MedicamentRepository;
import com.microservices.pharmacare.dao.repository.OrdonnanceRepository;
import com.microservices.pharmacare.dao.repository.PatientRepository;
import com.microservices.pharmacare.dto.PatientCreateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    // Injection des repositories
    private final PatientRepository patientRepository;
    private final OrdonnanceRepository ordonnanceRepository;
    private final MedicamentRepository medicamentRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository, OrdonnanceRepository ordonnanceRepository, MedicamentRepository medicamentRepository) {
        this.patientRepository = patientRepository;
        this.ordonnanceRepository = ordonnanceRepository;
        this.medicamentRepository = medicamentRepository;
    }

    // Obtenir toutes les ordonnances d'un patient par son ID
    public List<Ordonnance> getAllOrdonnancesByPatientId(Long patientId) {
        return ordonnanceRepository.findByPatientId(patientId);
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
            existingPatient.setCodePatient(patientUpdateDto.getCodePatient());
            return patientRepository.save(existingPatient);
        }
        return null;
    }

    // Supprimer un patient
    public void deletePatient(Long patientId) {
        patientRepository.deleteById(patientId);
    }

    public Patient getPatientByCode(String codePatient) {
        return patientRepository.findByCodePatient(codePatient).orElse(null);
    }


    // Obtenir l'historique des médicaments consommés par un patient
    public List<Medicament> getHistoriqueMédicamentsByPatientId(Long patientId) {
        return medicamentRepository.findByPatientId(patientId);
    }
}
