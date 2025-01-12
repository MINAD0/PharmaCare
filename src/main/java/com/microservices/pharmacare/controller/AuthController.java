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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
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
    public ResponseEntity<Map<String, String>> verifyCode(@PathVariable("codePatient") String codePatient) {
        boolean exists = patientService.verifyPatientCode(codePatient);
        Map<String, String> response = new HashMap<>();

        if (exists) {
            boolean isPasswordSet = patientService.isPasswordSet(codePatient);
            response.put("status", "success");

            if (isPasswordSet) {
                response.put("message", "Account already registered");
            } else {
                response.put("message", "Account exists but not yet registered");
            }

            response.put("passwordSet", isPasswordSet ? "true" : "false");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Code does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }



    // Method to set the password
    @PostMapping("/set-password")
    public boolean setPassword(@RequestBody Map<String, String> payload) {
        String codePatient = payload.get("codePatient");
        String newPassword = payload.get("newPassword");

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


    @PostMapping("/patient-login")
    public ResponseEntity<Map<String, String>> patientLogin(@RequestBody AuthRequest authRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Patient> optionalPatient = patientRepository.findByCodePatient(authRequest.getEmailOrCode());

            if (optionalPatient.isPresent()) {
                Patient patient = optionalPatient.get();

                // Check if the password matches
                if (passwordEncoder.matches(authRequest.getPassword(), patient.getMotDePasse())) {
                    // Generate a token
                    String token = jwtUtil.generateToken(patient.getCodePatient(), "PATIENT");
                    response.put("status", "success");
                    response.put("token", token);
                    response.put("message", "Login successful");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("status", "error");
                    response.put("message", "Invalid password");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
            } else {
                response.put("status", "error");
                response.put("message", "Patient not found with provided code");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An error occurred during login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}