package com.example.bankcards.security;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "bank.cards")
public class CardSecurityProperties {

    @NotBlank
    private String panHashSecret;

    public String getPanHashSecret() {
        return panHashSecret;
    }

    public void setPanHashSecret(String panHashSecret) {
        this.panHashSecret = panHashSecret;
    }
}