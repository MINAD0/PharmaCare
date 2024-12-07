package com.microservices.pharmacare.controller;


import com.microservices.pharmacare.dao.entities.Patient;
import com.microservices.pharmacare.dao.repository.PatientRepository;
import com.microservices.pharmacare.dto.AuthRequest;
import com.microservices.pharmacare.dto.AuthResponse;
import com.microservices.pharmacare.service.PatientService;
import com.microservices.pharmacare.service.PharmacienService;
import com.microservices.pharmacare.util.JwtUtil;
import org.springframework.http.HttpStatus;


import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PharmacienService pharmacienService;
    private final PatientService patientService;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;


    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, PharmacienService pharmacienService, PatientService patientService, PatientRepository patientRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.pharmacienService = pharmacienService;
        this.patientService = patientService;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        Authentication authentication;
        String role;

        if (authRequest.getEmailOrCode().equals("admin@example.com")) {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmailOrCode(), authRequest.getPassword()));
            role = "PHARMACIEN";
        } else {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmailOrCode(), authRequest.getPassword()));
            role = "PATIENT";
        }
        String token = jwtUtil.generateToken(authentication.getName(), role);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @GetMapping("/verify-code/{codePatient}")
    public ResponseEntity<String> verifyCode(@PathVariable("codePatient") String codePatient) {
        boolean exists = patientService.verifyPatientCode(codePatient);
        if (exists) {
            return ResponseEntity.ok("Code exists. Proceed to set the password.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid code.");
        }
    }

    // Method to set the password
    public boolean setPassword(String codePatient, String newPassword) {
        Optional<Patient> optionalPatient = patientRepository.findByCodePatient(codePatient);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            // Encrypt and set the new password
            patient.setMotDePasse(passwordEncoder.encode(newPassword));
            patientRepository.save(patient);
            return true; // Password update successful
        }
        return false; // Patient code not found
    }

}