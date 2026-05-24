package com.example.bankcards.service.validation;

import com.example.bankcards.entity.Card;
import com.example.bankcards.util.CardStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionValidator {

    private TransactionValidator() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void validateTransferBetweenUserCards(Long userId, Card sourceCard, Card targetCard, BigDecimal amount) {

        validateCards(sourceCard, targetCard);
        validateCardOwnership(userId, sourceCard, targetCard);
        validateAmount(amount);
        validateBalance(sourceCard, amount);
    }

    private static void validateCards(Card sourceCard, Card targetCard) {
        if (sourceCard.getId().equals(targetCard.getId())) {
            throw new IllegalArgumentException("Source and target cards must be different");
        }
        if (sourceCard.getCardStatus() != CardStatus.ACTIVE || targetCard.getCardStatus() != CardStatus.ACTIVE) {
            throw new IllegalArgumentException("One of cards is not active");
        }
    }

    private static void validateCardOwnership(Long userId, Card sourceCard, Card targetCard) {
        if (!sourceCard.getUser().getId().equals(userId) || !targetCard.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Both cards must belong to the user");
        }
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
    }

    private static void validateBalance(Card sourceCard, BigDecimal amount) {
        if (sourceCard.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
    }

}
