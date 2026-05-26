package com.example.bankcards.service;

import com.example.bankcards.dto.JwtResponse;
import com.example.bankcards.dto.LoginRequest;

public interface AuthService {
    JwtResponse authenticateUserAndGenerateToken(LoginRequest loginRequest);
}
