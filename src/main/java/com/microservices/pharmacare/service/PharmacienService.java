package com.microservices.pharmacare.service;

import com.microservices.pharmacare.dao.entities.Patient;
import com.microservices.pharmacare.dao.repository.OrdonnanceRepository;
import com.microservices.pharmacare.dao.repository.PatientRepository;
import com.microservices.pharmacare.dao.repository.PharmacienRepository;
import com.microservices.pharmacare.dto.OrdonnanceDTO;
import com.microservices.pharmacare.dto.PatientCreateDto;
import com.microservices.pharmacare.dto.PatientDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PharmacienService {

    private final PharmacienRepository pharmacienRepository;
    private final PatientRepository patientRepository;
    private final OrdonnanceRepository ordonnanceRepository;
    private final SmsService smsService;

    public PharmacienService(PharmacienRepository pharmacienRepository, PatientRepository patientRepository, OrdonnanceRepository ordonnanceRepository, SmsService smsService) {
        this.pharmacienRepository = pharmacienRepository;
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

    public List<OrdonnanceDTO> listOrdonnance(){
        try{
            return ordonnanceRepository.findAll().stream()
                    .map(ordonnance -> new OrdonnanceDTO(ordonnance.getId(), ordonnance.getDescription(), ordonnance.getDate()))
                    .collect(Collectors.toList());
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<PatientDTO> getPatientOrdonnances(String codePatient) {
        try{
            Optional<Patient> patient = patientRepository.findByCodePatient(codePatient);
            if (patient.isPresent()){
                return patient.get().getOrdonnances().stream()
                        .map(ordonnance -> new OrdonnanceDTO(ordonnance.getId(), ordonnance.getDescription(), ordonnance.getDate()))
                        .map(ordonnanceDTO -> new PatientDTO(
                                patient.get().getCodePatient(),
                                patient.get().getNom(),
                                patient.get().getPrenom(),
                                patient.get().getTel(),
                                patient.get().getCin(),
                                List.of(ordonnanceDTO)
                        ))
                        .collect(Collectors.toList());
            }else {
                return null;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
