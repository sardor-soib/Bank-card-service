package com.example.bankcards.util;

public enum CardType {

    DEBIT("Debit"),
    CREDIT("Credit");

    private final String displayName;

    CardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
