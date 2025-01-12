package com.microservices.pharmacare.controller;

import com.microservices.pharmacare.dao.entities.Patient;
import com.microservices.pharmacare.dto.PatientCreateDto;
import com.microservices.pharmacare.dto.PatientDTO;
import com.microservices.pharmacare.service.PatientService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
//@CrossOrigin("*")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{codePatient}")
    public ResponseEntity<PatientDTO> getPatientByCode(@PathVariable String codePatient) {
        PatientDTO patient = patientService.getPatientByCode(codePatient);
        if (patient != null) {
            return ResponseEntity.ok(patient);
        } else {
            return ResponseEntity.notFound().build();  // Si le patient n'est pas trouvé
        }
    }


    @PutMapping("/{codePatient}")
    public ResponseEntity<PatientDTO> updatePatientByCode(@PathVariable String codePatient,
                                                       @RequestBody PatientCreateDto patientCreateDto) {
        PatientDTO updatedPatient = patientService.updatePatient(codePatient, patientCreateDto);
        if (updatedPatient != null) {
            return ResponseEntity.ok(updatedPatient);
        } else {
            return ResponseEntity.notFound().build();  // Return 404 if the patient is not found
        }
    }

    @DeleteMapping("/{codePatient}")
    public ResponseEntity<Void> deletePatientByCode(@PathVariable String codePatient) {
        try {
            patientService.deletePatientByCode(codePatient);
            return ResponseEntity.noContent().build();  // Return empty response after deletion
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();  // Return 404 if the patient is not found
        }
    }


    @GetMapping("/{codePatient}/ordonnances")
    public ResponseEntity<?> getOrdonnancesByCodePatient(@PathVariable String codePatient) {
        return ResponseEntity.ok(patientService.getOrdonnancesByCodePatient(codePatient));
    }


    @GetMapping("/{codePatient}/historique-medicaments")
    public ResponseEntity<?> getHistoriqueMedicamentsByCodePatient(@PathVariable String codePatient) {
        try {
            return ResponseEntity.ok(patientService.getHistoriqueMedicamentsByCodePatient(codePatient));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();  // Return 404 if no patient or medications are found
        }
    }

}