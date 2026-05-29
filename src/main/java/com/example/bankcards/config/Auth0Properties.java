package com.example.bankcards.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "auth0")
public class Auth0Properties {

    private String domain;
    private String audience;

    public String getJwksUrl() {
        return "https://" + domain + "/.well-known/jwks.json";
    }

    public String getIssuer() {
        return "https://" + domain + "/";
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }
}