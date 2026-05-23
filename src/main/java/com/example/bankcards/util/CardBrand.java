package com.example.bankcards.util;

public enum CardBrand {

    VISA("Visa"),
    MASTERCARD("MasterCard"),
    AMERICAN_EXPRESS("American Express");

    private final String displayName;

    CardBrand(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
