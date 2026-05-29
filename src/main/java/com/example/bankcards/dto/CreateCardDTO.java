package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateCardDTO(
        @NotBlank String pan,
        String cardHolderName,
        @NotBlank String expirationDate,
        @NotBlank String cardBrand,
        @NotBlank String cardType,
        @NotNull @Positive BigDecimal balance,
        @NotBlank String cardStatus,
        @NotBlank String currency,
        @NotNull Long userId
) {
}