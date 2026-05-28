package com.example.bankcards.dto;

public record UserDTO(
        Long id,
        String fullName,
        String email,
        String phoneNumber,
        String password,
        String role
) {
    public static UserDTO.Builder builder() {
        return new UserDTO.Builder();
    }

    public static class Builder {
        private Long id;
        private String fullName;
        private String email;
        private String phoneNumber;
        private String password;
        private String role;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public UserDTO build() {
            return new UserDTO(id, fullName, email, phoneNumber, password, role);
        }
    }
}

