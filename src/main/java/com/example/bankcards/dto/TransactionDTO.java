package com.example.bankcards.dto;

import java.math.BigDecimal;

public record TransactionDTO(
        Long id,
        String type,
        BigDecimal amount,
        String date,
        Long cardId,
        Long userId
) {

    public static TransactionDTO.Builder builder() {
        return new TransactionDTO.Builder();
    }

    public static class Builder {
        private Long id;
        private String type;
        private BigDecimal amount;
        private String date;
        private Long cardId;
        private Long userId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Builder cardId(Long cardId) {
            this.cardId = cardId;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public TransactionDTO build() {
            return new TransactionDTO(id, type, amount, date, cardId, userId);
        }
    }
}
