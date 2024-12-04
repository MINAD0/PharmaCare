package com.microservices.pharmacare.service;

import com.microservices.pharmacare.dao.entities.Patient;
import com.microservices.pharmacare.dao.repository.PatientRepository;
import com.microservices.pharmacare.dao.repository.PharmacienRepository;
import com.microservices.pharmacare.dto.PatientCreateDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PharmacienService {

    private final PharmacienRepository pharmacienRepository;
    private final PatientRepository patientRepository;

    public PharmacienService(PharmacienRepository pharmacienRepository, PatientRepository patientRepository) {
        this.pharmacienRepository = pharmacienRepository;
        this.patientRepository = patientRepository;
    }

    public Patient createPatient(PatientCreateDto patientCreateDto) {
        Patient patient = new Patient();
        // Generate a simple codePatient
        String codePatient = generatePatientCode(patientCreateDto.getNom(), patientCreateDto.getPrenom());
        patient.setCodePatient(codePatient);
        patient.setNom(patientCreateDto.getNom());
        patient.setPrenom(patientCreateDto.getPrenom());
        patient.setTel(patientCreateDto.getTel());
        patient.setCin(patientCreateDto.getCin());
        patient.setMotDePasse(null); // Password is null by default
        return patientRepository.save(patient);
    }

    public List<Patient> ListPatients() {
        return patientRepository.findAll();
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



}
