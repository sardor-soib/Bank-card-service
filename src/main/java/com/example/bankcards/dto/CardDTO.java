package com.example.bankcards.dto;

import java.math.BigDecimal;

public record CardDTO(
        Long id,
        String maskedPan,
        String cardHolderName,
        String expirationDate,
        BigDecimal balance,
        String cardStatus,
        String currency,
        Long userId
) {

    public static CardDTO.Builder builder() {
        return new CardDTO.Builder();
    }

    public static class Builder {
        private Long id;
        private String maskedPan;
        private String cardHolderName;
        private String expirationDate;
        private BigDecimal balance;
        private String cardStatus;
        private String currency;
        private Long userId;

        public CardDTO.Builder id(Long id) {
            this.id = id;
            return this;
        }

        public CardDTO.Builder maskedPan(String maskedPan) {
            this.maskedPan = maskedPan;
            return this;
        }

        public CardDTO.Builder cardHolderName(String cardHolderName) {
            this.cardHolderName = cardHolderName;
            return this;
        }

        public CardDTO.Builder expirationDate(String expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public CardDTO.Builder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public CardDTO.Builder cardStatus(String cardStatus) {
            this.cardStatus = cardStatus;
            return this;
        }

        public CardDTO.Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public CardDTO.Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public CardDTO build() {
            return new CardDTO(id, maskedPan, cardHolderName, expirationDate, balance, cardStatus, currency, userId);
        }
    }
}