package com.example.bankcards.config;

import com.example.bankcards.security.Auth0Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties({Auth0Properties.class})
public class JwtDecoderConfig {
    private static final Logger logger = LoggerFactory.getLogger(JwtDecoderConfig.class);

    private final Auth0Properties auth0Properties;

    public JwtDecoderConfig(Auth0Properties auth0Properties) {
        this.auth0Properties = auth0Properties;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        String jwksUrl = auth0Properties.getJwksUrl();
        if (jwksUrl == null || jwksUrl.isEmpty()) {
            throw new IllegalStateException("auth0.jwks-url must be set in application.yml");
        }

        logger.info("Configuring JWT decoder with JWKS URL: {}", jwksUrl);

        RestTemplate restTemplate = new RestTemplate();

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwksUrl)
                .restOperations(restTemplate)
                .build();
        decoder.setJwtValidator(JwtValidators.createDefault());
        return decoder;
    }
}

