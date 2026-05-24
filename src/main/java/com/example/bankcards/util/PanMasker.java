package com.example.bankcards.util;

public final class PanMasker {

    private PanMasker() {
    }

    public static String mask(String pan) {
        String normalized = normalize(pan);
        String lastFour = normalized.substring(normalized.length() - 4);
        return "**** **** **** " + lastFour;
    }

    public static String bin(String pan) {
        return normalize(pan).substring(0, 6);
    }

    public static String lastFour(String pan) {
        String normalized = normalize(pan);
        return normalized.substring(normalized.length() - 4);
    }

    private static String normalize(String pan) {
        if (pan == null) {
            throw new IllegalArgumentException("PAN must not be null");
        }

        String normalized = pan.replaceAll("[\\s-]", "");

        if (!normalized.matches("\\d{12,19}")) {
            throw new IllegalArgumentException("PAN must contain 12 to 19 digits");
        }

        return normalized;
    }
}