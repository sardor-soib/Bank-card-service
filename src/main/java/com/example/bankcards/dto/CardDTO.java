package com.example.bankcards.dto;

public record CardDTO(
        Long id,
        String cardNumber,
        String cardHolderName,
        String expirationDate,
        String cvv
) {

    public static CardDTO.Builder builder() {
        return new CardDTO.Builder();
    }

    public static class Builder {
        private Long id;
        private String cardNumber;
        private String cardHolderName;
        private String expirationDate;
        private String cvv;

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

        public CardDTO.Builder cvv(String cvv) {
            this.cvv = cvv;
            return this;
        }

        public CardDTO build() {
            return new CardDTO(id, cardNumber, cardHolderName, expirationDate, cvv);
        }
    }
}
