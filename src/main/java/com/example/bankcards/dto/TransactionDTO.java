package com.example.bankcards.dto;

public record TransactionDTO(
        Long id,
        String type,
        Double amount,
        String date,
        Long cardId
) {

    public static TransactionDTO.Builder builder() {
        return new TransactionDTO.Builder();
    }

    public static class Builder {
        private Long id;
        private String type;
        private Double amount;
        private String date;
        private Long cardId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder amount(Double amount) {
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

        public TransactionDTO build() {
            return new TransactionDTO(id, type, amount, date, cardId);
        }
    }
}
