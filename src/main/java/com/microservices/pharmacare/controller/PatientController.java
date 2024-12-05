package com.microservices.pharmacare.controller;

import com.microservices.pharmacare.dao.entities.Patient;
import com.microservices.pharmacare.dto.PatientCreateDto;
import com.microservices.pharmacare.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    // Constructeur avec injection du service
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // Méthode pour récupérer un patient par son code unique
    @GetMapping("/{codePatient}")
    public ResponseEntity<Patient> getPatientByCode(@PathVariable String codePatient) {
        Patient patient = patientService.getPatientByCode(codePatient);
        if (patient != null) {
            return ResponseEntity.ok(patient);
        } else {
            return ResponseEntity.notFound().build();  // Si le patient n'est pas trouvé
        }
    }

    // Méthode pour mettre à jour un patient
    @PutMapping("/{patientId}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long patientId,
                                                 @RequestBody PatientCreateDto patientCreateDto) {
        Patient updatedPatient = patientService.updatePatient(patientId, patientCreateDto);
        if (updatedPatient != null) {
            return ResponseEntity.ok(updatedPatient);
        } else {
            return ResponseEntity.notFound().build();  // Si le patient n'est pas trouvé
        }
    }

    // Méthode pour supprimer un patient
    @DeleteMapping("/{patientId}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long patientId) {
        patientService.deletePatient(patientId);
        return ResponseEntity.noContent().build();  // Retourne une réponse vide après suppression
    }

    // Méthode pour récupérer les ordonnances d'un patient
    @GetMapping("/{patientId}/ordonnances")
    public ResponseEntity<?> getAllOrdonnancesByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getAllOrdonnancesByPatientId(patientId));
    }

    // Méthode pour obtenir l'historique des médicaments d'un patient
    @GetMapping("/{patientId}/historique-medicaments")
    public ResponseEntity<?> getHistoriqueMedicamentsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getHistoriqueMédicamentsByPatientId(patientId));
    }
}
