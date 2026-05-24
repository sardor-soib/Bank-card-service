package com.example.bankcards.dto;

import java.math.BigDecimal;

public record CardDTO(
        Long id,
        String cardNumber,
        String cardHolderName,
        String expirationDate,
        String cvvHash,
        BigDecimal balance,
        String cardStatus,
        Long userId
) {

    public static CardDTO.Builder builder() {
        return new CardDTO.Builder();
    }

    public static class Builder {
        private Long id;
        private String cardNumber;
        private String cardHolderName;
        private String expirationDate;
        private String cvvHash;
        private BigDecimal balance;
        private String cardStatus;
        private Long userId;

        public CardDTO.Builder id(Long id) {
            this.id = id;
            return this;
        }

        public CardDTO.Builder cardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
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

        public CardDTO.Builder cvvHash(String cvvHash) {
            this.cvvHash = cvvHash;
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

        public CardDTO.Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public CardDTO build() {
            return new CardDTO(id, cardNumber, cardHolderName, expirationDate, cvvHash, balance, cardStatus, userId);
        }
    }
}
