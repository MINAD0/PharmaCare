package com.microservices.pharmacare.controller;

import com.microservices.pharmacare.dao.entities.Patient;
import com.microservices.pharmacare.dto.PatientCreateDto;
import com.microservices.pharmacare.service.PharmacienService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pharmacien")
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
}
