package com.example.bankcards.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDTO(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank String phoneNumber,
        @NotBlank String password,
        String role
) {
}