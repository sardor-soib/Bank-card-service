package com.example.bankcards.dto;

import java.math.BigDecimal;

public record CreateCardDTO(
        String pan,
        String cardHolderName,
        String expirationDate,
        String cardBrand,
        String cardType,
        BigDecimal balance,
        String cardStatus,
        Long userId
) {
}