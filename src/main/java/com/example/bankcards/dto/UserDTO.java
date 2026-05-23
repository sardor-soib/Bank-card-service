package com.example.bankcards.dto;

public record UserDTO(
        Long id,
        String alias,
        String password,
        String role
) {
    public static UserDTO.Builder builder() {
        return new UserDTO.Builder();
    }

    public static class Builder {
        private Long id;
        private String alias;
        private String password;
        private String role;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder alias(String alias) {
            this.alias = alias;
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
            return new UserDTO(id, alias, password, role);
        }
    }
}

