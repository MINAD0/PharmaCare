package com.microservices.pharmacare.controller;

import com.microservices.pharmacare.dto.AuthRequest;
import com.microservices.pharmacare.dto.AuthResponse;
import com.microservices.pharmacare.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        Authentication authentication;
        String role;

        if (authRequest.getEmailOrCode().equals("admin@example.com")){
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmailOrCode(), authRequest.getPassword()));
            role = "PHARMACIEN";
        } else {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmailOrCode(), authRequest.getPassword()));
            role = "PATIENT";
        }
        String token = jwtUtil.generateToken(authentication.getName(), role);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
