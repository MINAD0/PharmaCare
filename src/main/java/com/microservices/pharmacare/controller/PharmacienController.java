package com.microservices.pharmacare.controller;

import com.microservices.pharmacare.dao.entities.Ordonnance;
import com.microservices.pharmacare.dao.entities.Patient;
import com.microservices.pharmacare.dto.*;
import com.microservices.pharmacare.service.MedicamentService;
import com.microservices.pharmacare.service.OrdonnanceService;
import com.microservices.pharmacare.service.PharmacienService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/pharmacien")
@CrossOrigin("*")
public class PharmacienController {
    private final PharmacienService pharmacienService;
    private final MedicamentService medicamentService;
    private final OrdonnanceService ordonnanceService;

    public PharmacienController(PharmacienService pharmacienService, MedicamentService medicamentService, OrdonnanceService ordonnanceService) {
        this.pharmacienService = pharmacienService;
        this.medicamentService = medicamentService;
        this.ordonnanceService = ordonnanceService;
    }


    @PostMapping("/patient")
    public ResponseEntity<Patient> createPatient(@RequestBody PatientCreateDto patientCreateDto) {
        Patient savedPatient = pharmacienService.createPatient(patientCreateDto);
        return ResponseEntity.ok(savedPatient);
    }


    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getPatients() {
        List<Patient> patients = pharmacienService.ListPatients();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/patient/{codePatient}")
    public ResponseEntity<Optional<PatientDTO>> getPatient(@PathVariable("codePatient")  String codePatient) {
        Optional<PatientDTO> patient = pharmacienService.getPatientByCode(codePatient);
        if (patient.isPresent()){
            return ResponseEntity.ok(patient);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/medicament")
    public ResponseEntity<List<MedicamentDTO>> getAllMedicaments() {
        List<MedicamentDTO> medicaments = medicamentService.getAllMedicaments();
        return ResponseEntity.ok(medicaments);
    }

//    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/ordonnances")
    public ResponseEntity<List<OrdonnanceDTO>> getOrdonnances() {
        List<OrdonnanceDTO> ordonnances = pharmacienService.listOrdonnance();
        return ResponseEntity.ok(ordonnances);
    }

    @GetMapping("/patient/{codePatient}/ordonnances")
    public ResponseEntity<List<PatientDTO>> getPatientOrdonnances(@PathVariable("codePatient")  String codePatient) {
        List<PatientDTO> patient = pharmacienService.getPatientOrdonnances(codePatient);
        if (patient != null){
            return ResponseEntity.ok(patient);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/ordonnance")
    public ResponseEntity<OrdonnanceDTO> createOrdonnance(@RequestBody OrdonnanceCreateDTO ordonnanceCreateDTO) {
        OrdonnanceDTO createdOrdonnance = ordonnanceService.createOrdonnance(ordonnanceCreateDTO);
        return ResponseEntity.ok(createdOrdonnance);
    }

    @GetMapping("/patient/{codePatient}/ordonnance")
    public ResponseEntity<List<OrdonnanceDTO>> getOrdonnancesByPatient(@PathVariable String codePatient) {
        List<OrdonnanceDTO> ordonnances = ordonnanceService.getAllOrdonnancesByPatient(codePatient);
        return ResponseEntity.ok(ordonnances);
    }


    @PostMapping("medicament")
    public ResponseEntity<MedicamentDTO> createMedicament(
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        MedicamentCreateDTO dto = new MedicamentCreateDTO();
        dto.setNom(nom);
        dto.setDescription(description);
        dto.setImageFile(imageFile);

        MedicamentDTO createdMedicament = medicamentService.createMedicament(dto);
        return ResponseEntity.ok(createdMedicament);
    }

    @GetMapping("/patient/{codePatient}/rappels")
    public ResponseEntity<List<RappelDTO>> getPatientRappels(@PathVariable String codePatient) {
        List<RappelDTO> rappels = ordonnanceService.getRappelsByPatient(codePatient);
        return ResponseEntity.ok(rappels);
    }

    @PutMapping("/rappel/{rappelId}")
    public ResponseEntity<String> updateRappelStatus(
            @PathVariable Long rappelId,
            @RequestBody Map<String, Boolean> requestBody) {

        boolean newStatus = requestBody.get("status");
        ordonnanceService.updateRappelStatus(rappelId, newStatus);
        return ResponseEntity.ok("Rappel status updated successfully");
    }

}
