package com.example.bankcards.security;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class PanHashEncoder {

    private final String secret;

    public PanHashEncoder(@Value("${bank.cards.pan-hash-secret}") String secret) {
        this.secret = secret;
    }

    public String encode(String pan) {
        String normalizedPan = normalize(pan);
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret).hmacHex(normalizedPan);
    }

    public boolean matches(String rawPan, String panHash) {
        return MessageDigest.isEqual(
                encode(rawPan).getBytes(StandardCharsets.UTF_8),
                panHash.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String normalize(String pan) {
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
