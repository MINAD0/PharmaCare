package com.microservices.pharmacare.controller;

import com.microservices.pharmacare.dao.entities.Ordonnance;
import com.microservices.pharmacare.dao.entities.Patient;
import com.microservices.pharmacare.dto.OrdonnanceCreateDTO;
import com.microservices.pharmacare.dto.OrdonnanceDTO;
import com.microservices.pharmacare.dto.PatientCreateDto;
import com.microservices.pharmacare.dto.PatientDTO;
import com.microservices.pharmacare.service.PharmacienService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pharmacien")
@CrossOrigin("*")
public class PharmacienController {
    private final PharmacienService pharmacienService;

    public PharmacienController(PharmacienService pharmacienService) {
        this.pharmacienService = pharmacienService;
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

    // Endpoint to create Ordonnance with Medicaments
    @Operation(summary = "Create Ordonnance with Medicaments", description = "Create an ordonnance and associate it with medicaments for a specific patient.")
    @PostMapping("/ordonnance")
    public ResponseEntity<OrdonnanceDTO> createOrdonnanceWithMedicaments(@RequestBody OrdonnanceCreateDTO ordonnanceCreateDTO) {
        try {
            OrdonnanceDTO ordonnanceDTO = pharmacienService.createOrdonnanceWithMedicaments(ordonnanceCreateDTO);
            return new ResponseEntity<>(ordonnanceDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            // Handle exceptions
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
