package com.example.bankcards.service.validation;

import com.example.bankcards.entity.Card;

public class UserValidator {

    private UserValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static void validateOwner(Long userId, Card card) {
        if (!userId.equals(card.getUser().getId())) {
            throw new IllegalArgumentException("User is not the owner of the card");
        }
    }
}
