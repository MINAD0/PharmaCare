package com.microservices.pharmacare.controller;

import com.microservices.pharmacare.dao.entities.Patient;
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

//    @SecurityRequirement(name = "BearerAuth")

    @GetMapping("/patient/{codePatient}/ordonnances")
    public ResponseEntity<List<PatientDTO>> getPatientOrdonnances(@PathVariable("codePatient")  String codePatient) {
        List<PatientDTO> patient = pharmacienService.getPatientOrdonnances(codePatient);
        if (patient != null){
            return ResponseEntity.ok(patient);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


}
