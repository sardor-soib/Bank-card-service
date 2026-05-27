package com.example.bankcards.service;

import com.example.bankcards.dto.JwtResponse;
import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.dto.RegisterRequest;

public interface AuthService {
    JwtResponse registerUserAndGenerateToken(RegisterRequest registerRequest);

    JwtResponse authenticateUserAndGenerateToken(LoginRequest loginRequest);
}
