package com.microservices.pharmacare.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AuthRequest {
    private String emailOrCode;
    private String password;
}
