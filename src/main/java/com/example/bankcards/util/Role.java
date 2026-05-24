package com.example.bankcards.util;

public enum Role {

    ADMIN("Admin"),
    USER("User");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
